
package com.lab.concurrencypalallelism.marketInformation.infra.persistence.adapter;

import com.lab.concurrencypalallelism.marketInformation.application.port.out.ExchangeRatePersistence;
import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import com.lab.concurrencypalallelism.marketInformation.domain.model.ExchangeRate;
import com.lab.concurrencypalallelism.marketInformation.infra.metrics.MarketCacheMetrics;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.mapper.ExchangeRateMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.mapper.ExchangeRateCacheMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.ExchangeRateCacheValue;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.repository.ExchangeRateCacheRepository;
import com.lab.concurrencypalallelism.marketInformation.infra.retry.RetryExchangeRateQuery;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ExchangeRatePersistenceAdapter implements ExchangeRatePersistence {

  private static final Logger log = LoggerFactory.getLogger(ExchangeRatePersistenceAdapter.class);

  private final RetryExchangeRateQuery retryExchangeRateQuery;
  private final ExchangeRateCacheRepository exchangeRateCacheRepository;
  private final ExchangeRateMapper exchangeRateMapper;
  private final ExchangeRateCacheMapper exchangeRateCacheMapper;
  private final MarketCacheMetrics marketCacheMetrics;

  public ExchangeRatePersistenceAdapter(
      RetryExchangeRateQuery retryExchangeRateQuery,
      ExchangeRateCacheRepository exchangeRateCacheRepository,
      ExchangeRateMapper exchangeRateMapper,
      ExchangeRateCacheMapper exchangeRateCacheMapper,
      MarketCacheMetrics marketCacheMetrics) {
    this.retryExchangeRateQuery = retryExchangeRateQuery;
    this.exchangeRateCacheRepository = exchangeRateCacheRepository;
    this.exchangeRateMapper = exchangeRateMapper;
    this.exchangeRateCacheMapper = exchangeRateCacheMapper;
    this.marketCacheMetrics = marketCacheMetrics;
  }

  @Override
  public List<ExchangeRate> findByCurrencies(Currency fromCurrency, Currency toCurrency) {

    try {
      Optional<List<ExchangeRateCacheValue>> cachedExchangeRates =
          exchangeRateCacheRepository.findByCurrencies(fromCurrency, toCurrency);

      if (cachedExchangeRates.isPresent()) {
        marketCacheMetrics.recordHit("exchange_rate");
        return cachedExchangeRates.get().stream()
            .map(exchangeRateCacheMapper::toDomain)
            .toList();
      }

      marketCacheMetrics.recordMiss("exchange_rate");

    } catch (RuntimeException exception) {
      log.warn("Error al consultar Redis", exception);
    }

    List<ExchangeRate> exchangeRateList = retryExchangeRateQuery
        .findByCurrencies(fromCurrency, toCurrency)
        .stream()
        .map(exchangeRateMapper::toDomain)
        .toList();

    List<ExchangeRateCacheValue> exchangeRatesToCache = exchangeRateList.stream()
        .map(exchangeRateCacheMapper::toCacheValue)
        .toList();

    try {
      exchangeRateCacheRepository.save(fromCurrency, toCurrency, exchangeRatesToCache);
    } catch (RuntimeException exception) {
      log.warn("Error al guardar tipos de cambio en Redis", exception);
    }

    return exchangeRateList;
  }
}
