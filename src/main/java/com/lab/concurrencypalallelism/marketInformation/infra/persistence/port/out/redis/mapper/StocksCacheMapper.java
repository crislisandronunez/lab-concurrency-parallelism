package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.mapper;

import com.lab.concurrencypalallelism.marketInformation.domain.model.Stocks;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.StockCacheValue;
import org.springframework.stereotype.Component;

@Component
public class StocksCacheMapper {

  public StockCacheValue toCacheValue(Stocks stock) {
    return new StockCacheValue(
        stock.getSymbol(),
        stock.getPrice(),
        stock.getChangePercent(),
        stock.getVolume(),
        stock.getUpdateAt()
    );
  }

  public Stocks toDomain(StockCacheValue value) {
    return Stocks.restore(
        value.symbol(),
        value.price(),
        value.changePercent(),
        value.volume(),
        value.updateAt()
    );
  }
}
