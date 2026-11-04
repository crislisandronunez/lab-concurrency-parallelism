package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExchangeRateCacheValue(
    UUID id,
    Currency fromCurrency,
    Currency toCurrency,
    BigDecimal rate,
    Instant updateAt
) {

}
