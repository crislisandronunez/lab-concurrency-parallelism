package com.lab.concurrencypalallelism.marketInformation.infra.rest.dto;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import com.lab.concurrencypalallelism.marketInformation.domain.enums.Sentiment;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record MarketInformationResponse(

    StockResponse stockResult,
    List<ExchangeRateResponse> exchangeRateResult,
    List<MarketNewsResponse> marketNewsResult
) {






}
