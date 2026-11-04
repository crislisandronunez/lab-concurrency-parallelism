package com.lab.concurrencypalallelism.marketInformation.application.result;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeRateResult(
    Currency fromCurrency,
    Currency toCurrency,
    BigDecimal rate,
    Instant updatedAt
) {
}
