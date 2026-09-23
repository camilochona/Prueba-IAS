package com.bancoIas.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("TRANSFER")
public class Transfer {
    @Id
    private Long id;
    private String requestReference;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private String status;
    private String reason;
    private LocalDateTime createdAt;
}
