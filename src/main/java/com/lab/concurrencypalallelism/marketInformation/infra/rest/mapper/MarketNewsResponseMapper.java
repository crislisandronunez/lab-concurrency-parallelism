package com.lab.concurrencypalallelism.marketInformation.infra.rest.mapper;

import com.lab.concurrencypalallelism.marketInformation.application.result.MarketNewsResult;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.dto.MarketNewsResponse;
import org.springframework.stereotype.Component;

@Component("marketNewsResponseMapper")
public class MarketNewsResponseMapper {

  public MarketNewsResponse toResponse(MarketNewsResult result) {
    return new MarketNewsResponse(
        result.title(),
        result.source(),
        result.timestamp(),
        result.sentiment()
    );
  }
}
