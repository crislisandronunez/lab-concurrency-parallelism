package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.mapper;

import com.lab.concurrencypalallelism.marketInformation.domain.model.ExchangeRate;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.ExchangeRateCacheValue;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateCacheMapper {

  public ExchangeRateCacheValue toCacheValue(ExchangeRate exchangeRate) {
    return new ExchangeRateCacheValue(
        exchangeRate.getId(),
        exchangeRate.getFromCurrency(),
        exchangeRate.getToCurrency(),
        exchangeRate.getRate(),
        exchangeRate.getUpdateAt()
    );
  }

  public ExchangeRate toDomain(ExchangeRateCacheValue cachedExchangeRate) {
    return ExchangeRate.restore(
        cachedExchangeRate.id(),
        cachedExchangeRate.fromCurrency(),
        cachedExchangeRate.toCurrency(),
        cachedExchangeRate.rate(),
        cachedExchangeRate.updateAt()
    );
  }
}
