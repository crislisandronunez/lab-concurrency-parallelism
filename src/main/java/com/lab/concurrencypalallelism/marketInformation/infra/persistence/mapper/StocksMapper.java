package com.lab.concurrencypalallelism.marketInformation.infra.persistence.mapper;

import com.lab.concurrencypalallelism.marketInformation.domain.model.Stocks;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity.StocksEntity;
import org.springframework.stereotype.Component;

@Component
public class StocksMapper {

  public StocksEntity toEntity(Stocks stock) {
    return new StocksEntity(
        stock.getSymbol(),
        stock.getPrice(),
        stock.getChangePercent(),
        stock.getVolume(),
        stock.getUpdateAt()
    );
  }

  public Stocks toDomain(StocksEntity entity) {
    return Stocks.restore(
        entity.getSymbol(),
        entity.getPrice(),
        entity.getChangePercent(),
        entity.getVolume(),
        entity.getUpdateAt()
    );
  }
}
