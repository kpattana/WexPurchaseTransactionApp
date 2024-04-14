package org.wex.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class PurchaseOrder {
    private long id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal totalInUsd;
    private String targetCurrency;
    private String targetCountry;
    private BigDecimal exchangeRate;
    private BigDecimal  totalInTargetCurrency;
    private LocalDate currencyRecordDate;
}
