package com.lab.concurrencypalallelism.marketInformation.infra.rest.dto;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Sentiment;
import java.time.Instant;

public record MarketNewsResponse(
    String title,
    String source,
    Instant timestamp,
    Sentiment sentiment
) {
}