package com.lab.concurrencypalallelism.marketInformation.infra.rest.mapper;

import com.lab.concurrencypalallelism.marketInformation.application.result.ExchangeRateResult;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.dto.ExchangeRateResponse;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateMapper {

  public ExchangeRateResponse toResponse(ExchangeRateResult result) {
    return new ExchangeRateResponse(
        result.fromCurrency(),
        result.toCurrency(),
        result.rate(),
        result.updatedAt()
    );
  }
}
