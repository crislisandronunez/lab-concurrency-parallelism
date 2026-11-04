package com.lab.concurrencypalallelism.marketInformation.application.result;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Sentiment;
import java.time.Instant;

public record MarketNewsResult(
    String title,
    String source,
    Instant timestamp,
    Sentiment sentiment
) {
}
