package com.bancoIas;

import com.bancoIas.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class BancolasApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testSuccessfulTransfer() {
        TransferRequest request = new TransferRequest();
        request.setRequestReference("TEST-REF-001");
        request.setSourceAccountId("CTA-1001");
        request.setDestinationAccountId("CTA-2001");
        request.setAmount(new BigDecimal("100000"));

        webTestClient.post()
                .uri("/api/transfers")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("AUTORIZADA")
                .jsonPath("$.requestReference").isEqualTo("TEST-REF-001");
    }

    @Test
    void testDailyLimitExceeded() {
        TransferRequest request = new TransferRequest();
        request.setRequestReference("TEST-REF-LIMIT");
        request.setSourceAccountId("CTA-1002");
        request.setDestinationAccountId("CTA-2001");
        request.setAmount(new BigDecimal("6000000")); // Supera los 5 millones

        webTestClient.post()
                .uri("/api/transfers")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("RECHAZADA")
                .jsonPath("$.reason").isEqualTo("Excede el límite diario permitido");
    }

}
