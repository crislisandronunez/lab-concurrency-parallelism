package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.repository;

import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.StockCacheValue;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

@Repository
public class StocksCacheRepository {

  private static final Duration TTL = Duration.ofSeconds(30);
  private static final String KEY_PREFIX = "stock:";

  private final RedisTemplate<String, String> redisTemplate;
  private final JsonMapper jsonMapper;

  public StocksCacheRepository(
      RedisTemplate<String, String> redisTemplate,
      JsonMapper jsonMapper) {
    this.redisTemplate = redisTemplate;
    this.jsonMapper = jsonMapper;
  }

  public Optional<StockCacheValue> findBySymbol(String symbol) {
    String json = redisTemplate.opsForValue().get(KEY_PREFIX + symbol);

    if (json == null) {
      return Optional.empty();
    }

    StockCacheValue value = jsonMapper.readValue(json, StockCacheValue.class);

    return Optional.of(value);
  }

  public void save(StockCacheValue value) {
    String json = jsonMapper.writeValueAsString(value);

    redisTemplate.opsForValue().set(
        KEY_PREFIX + value.symbol(),
        json,
        TTL
    );
  }
}
