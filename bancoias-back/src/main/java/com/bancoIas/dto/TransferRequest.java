package com.bancoIas.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String requestReference;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
}
