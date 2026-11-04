package com.lab.concurrencypalallelism.marketInformation.application.result;

import java.util.List;

public record MarketInformationResult(

    StockResult stockResult,
    List<ExchangeRateResult> exchangeRateResult,
    List<MarketNewsResult> marketNewsResult

) {
}
