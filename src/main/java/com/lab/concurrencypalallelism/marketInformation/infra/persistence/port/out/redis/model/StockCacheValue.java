package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model;

import java.math.BigDecimal;
import java.time.Instant;

public record StockCacheValue(
    String symbol,
    BigDecimal price,
    BigDecimal changePercent,
    Long volume,
    Instant updateAt
) {

}
