package com.bancoIas.repository;

import com.bancoIas.model.Transfer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TransferRepository extends ReactiveCrudRepository<Transfer, Long> {
    Mono<Transfer> findByRequestReference(String requestReference);
}
