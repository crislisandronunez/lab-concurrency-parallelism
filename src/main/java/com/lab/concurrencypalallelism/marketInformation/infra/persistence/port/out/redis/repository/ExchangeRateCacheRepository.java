package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.repository;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.ExchangeRateCacheValue;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Repository
public class ExchangeRateCacheRepository {

  private static final Duration TTL = Duration.ofSeconds(30);
  private static final String KEY_PREFIX = "exchange-rate:";

  private final RedisTemplate<String, String> redisTemplate;
  private final JsonMapper jsonMapper;

  public ExchangeRateCacheRepository(
      RedisTemplate<String, String> redisTemplate,
      JsonMapper jsonMapper) {
    this.redisTemplate = redisTemplate;
    this.jsonMapper = jsonMapper;
  }

  public Optional<List<ExchangeRateCacheValue>> findByCurrencies(
      Currency fromCurrency,
      Currency toCurrency) {

    String key = buildKey(fromCurrency, toCurrency);
    String json = redisTemplate.opsForValue().get(key);

    if (json == null) {
      return Optional.empty();
    }

    List<ExchangeRateCacheValue> cachedExchangeRates = jsonMapper.readValue(
        json,
        new TypeReference<List<ExchangeRateCacheValue>>() {}
    );

    return Optional.of(cachedExchangeRates);
  }

  public void save(
      Currency fromCurrency,
      Currency toCurrency,
      List<ExchangeRateCacheValue> exchangeRatesToCache) {

    String key = buildKey(fromCurrency, toCurrency);
    String json = jsonMapper.writeValueAsString(exchangeRatesToCache);

    redisTemplate.opsForValue().set(
        key,
        json,
        TTL
    );
  }

  private String buildKey(Currency fromCurrency, Currency toCurrency) {

    String source = fromCurrency == null ? "ALL" : fromCurrency.name();
    String target = toCurrency == null ? "ALL" : toCurrency.name();

    return KEY_PREFIX + source + ":" + target;
  }
}
