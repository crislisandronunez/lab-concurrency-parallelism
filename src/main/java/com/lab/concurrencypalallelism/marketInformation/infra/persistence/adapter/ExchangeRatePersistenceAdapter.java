package com.lab.concurrencypalallelism.marketInformation.infra.persistence.adapter;

import com.lab.concurrencypalallelism.marketInformation.application.port.out.ExchangeRatePersistence;
import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import com.lab.concurrencypalallelism.marketInformation.domain.model.ExchangeRate;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.mapper.ExchangeRateMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.postgres.ExchangeRateRepository;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.mapper.ExchangeRateCacheMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.ExchangeRateCacheValue;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.repository.ExchangeRateCacheRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ExchangeRatePersistenceAdapter implements ExchangeRatePersistence {

  private final ExchangeRateRepository exchangeRateRepository;
  private final ExchangeRateCacheRepository exchangeRateCacheRepository;
  private final ExchangeRateMapper exchangeRateMapper;
  private final ExchangeRateCacheMapper exchangeRateCacheMapper;

  public ExchangeRatePersistenceAdapter(
      ExchangeRateRepository exchangeRateRepository,
      ExchangeRateCacheRepository exchangeRateCacheRepository,
      ExchangeRateMapper exchangeRateMapper,
      ExchangeRateCacheMapper exchangeRateCacheMapper) {
    this.exchangeRateRepository = exchangeRateRepository;
    this.exchangeRateCacheRepository = exchangeRateCacheRepository;
    this.exchangeRateMapper = exchangeRateMapper;
    this.exchangeRateCacheMapper = exchangeRateCacheMapper;
  }

  @Override
  public List<ExchangeRate> findByCurrencies(Currency fromCurrency, Currency toCurrency) {

    Optional<List<ExchangeRateCacheValue>> cachedExchangeRates =
        exchangeRateCacheRepository.findByCurrencies(fromCurrency, toCurrency);

    if (cachedExchangeRates.isPresent()) {
      return cachedExchangeRates.get().stream()
          .map(exchangeRateCacheMapper::toDomain)
          .toList();
    }

    List<ExchangeRate> exchangeRateList = exchangeRateRepository
        .findByCurrencies(fromCurrency, toCurrency)
        .stream()
        .map(exchangeRateMapper::toDomain)
        .toList();

    List<ExchangeRateCacheValue> exchangeRatesToCache = exchangeRateList.stream()
        .map(exchangeRateCacheMapper::toCacheValue)
        .toList();

    exchangeRateCacheRepository.save(fromCurrency, toCurrency, exchangeRatesToCache);

    return exchangeRateList;
  }
}
