package org.wex.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateData(String country, String currency, BigDecimal exchange_rate, LocalDate record_date) {
}
