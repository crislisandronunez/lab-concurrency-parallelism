package com.lab.concurrencypalallelism.marketInformation.infra.rest.mapper;

import com.lab.concurrencypalallelism.marketInformation.application.command.GetMarketInformationCommand;
import com.lab.concurrencypalallelism.marketInformation.application.result.MarketInformationResult;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.dto.GetMarketInformationRequest;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.dto.MarketInformationResponse;
import org.springframework.stereotype.Component;

@Component
public class MarketInformationMapper {

  private final StockMapper stockMapper;
  private final ExchangeRateMapper exchangeRateMapper;
  private final MarketNewsMapper marketNewsMapper;

  public MarketInformationMapper(
      StockMapper stockMapper,
      ExchangeRateMapper exchangeRateMapper,
      MarketNewsMapper marketNewsMapper) {
    this.stockMapper = stockMapper;
    this.exchangeRateMapper = exchangeRateMapper;
    this.marketNewsMapper = marketNewsMapper;
  }

  public GetMarketInformationCommand toCommand(GetMarketInformationRequest request) {
    return new GetMarketInformationCommand(
        request.symbol(),
        request.fromCurrency(),
        request.toCurrency()
    );
  }

  public MarketInformationResponse toResponse(MarketInformationResult result) {
    return new MarketInformationResponse(
        stockMapper.toResponse(result.stockResult()),
        result.exchangeRateResult().stream()
            .map(exchangeRateMapper::toResponse)
            .toList(),
        result.marketNewsResult().stream()
            .map(marketNewsMapper::toResponse)
            .toList()
    );
  }
}
