package com.bancoIas.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Table("ACCOUNT")
public class Account {
    @Id
    private String id;
    private BigDecimal dailyLimit;
    private BigDecimal accumulatedDaily;

    @Version
    private Long version;
}
