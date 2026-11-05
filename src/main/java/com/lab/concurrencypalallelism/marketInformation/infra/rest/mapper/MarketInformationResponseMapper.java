package com.lab.concurrencypalallelism.marketInformation.infra.rest.mapper;

import com.lab.concurrencypalallelism.marketInformation.application.command.GetMarketInformationCommand;
import com.lab.concurrencypalallelism.marketInformation.application.result.MarketInformationResult;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.dto.GetMarketInformationRequest;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.dto.MarketInformationResponse;
import org.springframework.stereotype.Component;

@Component("MarketInformationResponseMapper")
public class MarketInformationResponseMapper {

  private final StockResponseMapper stockMapper;
  private final ExchangeRateResponseMapper exchangeRateResponseMapper;
  private final MarketNewsResponseMapper marketNewsMapper;

  public MarketInformationResponseMapper(
      StockResponseMapper stockMapper,
      ExchangeRateResponseMapper exchangeRateResponseMapper,
      MarketNewsResponseMapper marketNewsMapper) {
    this.stockMapper = stockMapper;
    this.exchangeRateResponseMapper = exchangeRateResponseMapper;
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
            .map(exchangeRateResponseMapper::toResponse)
            .toList(),
        result.marketNewsResult().stream()
            .map(marketNewsMapper::toResponse)
            .toList()
    );
  }
}
