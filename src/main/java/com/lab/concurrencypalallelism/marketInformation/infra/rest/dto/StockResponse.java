package com.lab.concurrencypalallelism.marketInformation.infra.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record StockResponse(
    String symbol,
    BigDecimal price,
    BigDecimal changePercent,
    Long volume,
    Instant updateAt
) {
}