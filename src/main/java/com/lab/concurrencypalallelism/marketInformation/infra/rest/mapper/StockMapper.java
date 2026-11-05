package com.lab.concurrencypalallelism.marketInformation.infra.rest.mapper;

import com.lab.concurrencypalallelism.marketInformation.application.result.StockResult;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.dto.StockResponse;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

  public StockResponse toResponse(StockResult result) {
    return new StockResponse(
        result.symbol(),
        result.price(),
        result.changePercent(),
        result.volume(),
        result.updateAt()
    );
  }
}
