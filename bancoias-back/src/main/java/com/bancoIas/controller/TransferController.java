package com.bancoIas.controller;

import com.bancoIas.dto.TransferRequest;
import com.bancoIas.model.Transfer;
import com.bancoIas.repository.TransferRepository;
import com.bancoIas.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "*") // Permite peticiones desde el localhost de Angular
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final TransferRepository transferRepository;

    @PostMapping
    public Mono<Transfer> createTransfer(@RequestBody TransferRequest request) {
        return transferService.processTransfer(request);
    }

    @GetMapping
    public Flux<Transfer> getRecentTransfers() {
        // RF06: Consultar transferencias recientes
        return transferRepository.findAll();
    }
}
