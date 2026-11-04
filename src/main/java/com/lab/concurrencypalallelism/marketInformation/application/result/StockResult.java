package com.lab.concurrencypalallelism.marketInformation.application.result;

import java.math.BigDecimal;
import java.time.Instant;

public record StockResult (
    String symbol,
    BigDecimal price,
    BigDecimal changePercent,
    Long volume,
    Instant updateAt
){
}
