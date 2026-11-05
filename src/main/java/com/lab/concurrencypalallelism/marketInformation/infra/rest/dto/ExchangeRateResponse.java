package com.lab.concurrencypalallelism.marketInformation.infra.rest.dto;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeRateResponse(
    Currency fromCurrency,
    Currency toCurrency,
    BigDecimal rate,
    Instant updatedAt
) {
}
