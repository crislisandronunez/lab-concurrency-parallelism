package com.lab.concurrencypalallelism.marketInformation.infra.persistence.mapper;

import com.lab.concurrencypalallelism.marketInformation.domain.model.ExchangeRate;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity.ExchangeRateEntity;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateMapper {

  public ExchangeRateEntity toEntity(ExchangeRate exchangeRate) {
    return new ExchangeRateEntity(
        exchangeRate.getId(),
        exchangeRate.getFromCurrency(),
        exchangeRate.getToCurrency(),
        exchangeRate.getRate(),
        exchangeRate.getUpdateAt()
    );
  }

  public ExchangeRate toDomain(ExchangeRateEntity entity) {
    return ExchangeRate.restore(
        entity.getId(),
        entity.getFromCurrency(),
        entity.getToCurrency(),
        entity.getRate(),
        entity.getUpdateAt()
    );
  }
}
