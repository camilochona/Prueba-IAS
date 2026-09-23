package com.bancoIas.service;

import com.bancoIas.dto.TransferRequest;
import com.bancoIas.model.Transfer;

import com.bancoIas.repository.AccountRepository;
import com.bancoIas.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public Mono<Transfer> processTransfer(TransferRequest req) {
        // RF05: Idempotencia - Si ya existe, se devuelve tal cual sin procesar de nuevo
        return transferRepository.findByRequestReference(req.getRequestReference())
                .switchIfEmpty(Mono.defer(() -> executeTransfer(req)));
    }

    private Mono<Transfer> executeTransfer(TransferRequest req) {
        // RF02: Validaciones básicas
        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return saveTransfer(req, "RECHAZADA", "El monto debe ser mayor a cero");
        }
        if (req.getSourceAccountId().equals(req.getDestinationAccountId())) {
            return saveTransfer(req, "RECHAZADA", "Cuenta origen y destino deben ser diferentes");
        }

        return accountRepository.findById(req.getSourceAccountId())
                .switchIfEmpty(Mono.error(new RuntimeException("Cuenta origen no existe")))
                .flatMap(account -> {
                    BigDecimal newAccumulated = account.getAccumulatedDaily().add(req.getAmount());

                    // RF02: Validar límite diario
                    if (newAccumulated.compareTo(account.getDailyLimit()) > 0) {
                        return saveTransfer(req, "RECHAZADA", "Excede el límite diario permitido");
                    }

                    // Actualizar límite acumulado
                    account.setAccumulatedDaily(newAccumulated);

                    // RF04: Manejo de concurrencia con reintentos si dos hilos chocan
                    return accountRepository.save(account)
                            .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                                    .filter(e -> e instanceof OptimisticLockingFailureException))
                            .then(saveTransfer(req, "AUTORIZADA", "Transferencia exitosa"));
                })
                .onErrorResume(e -> saveTransfer(req, "RECHAZADA", e.getMessage()));
    }

    private Mono<Transfer> saveTransfer(TransferRequest req, String status, String reason) {
        Transfer t = new Transfer();
        t.setRequestReference(req.getRequestReference());
        t.setSourceAccountId(req.getSourceAccountId());
        t.setDestinationAccountId(req.getDestinationAccountId());
        t.setAmount(req.getAmount());
        t.setStatus(status);
        t.setReason(reason);
        t.setCreatedAt(LocalDateTime.now());
        return transferRepository.save(t); // RF03: Conservar resultado
    }
}
