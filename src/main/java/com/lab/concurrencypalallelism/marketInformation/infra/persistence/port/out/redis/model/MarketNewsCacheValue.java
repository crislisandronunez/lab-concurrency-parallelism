package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Sentiment;
import java.time.Instant;
import java.util.UUID;

public record MarketNewsCacheValue(
    UUID id,
    String title,
    String source,
    Instant timestamp,
    Sentiment sentiment
) {

}
