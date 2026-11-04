package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.repository;

import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.MarketNewsCacheValue;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Repository
public class MarketNewsCacheRepository {

  private static final Duration TTL = Duration.ofSeconds(30);
  private static final String KEY_LATEST_FIVE = "market-news:latest-five";

  private final RedisTemplate<String, String> redisTemplate;
  private final JsonMapper jsonMapper;

  public MarketNewsCacheRepository(
      RedisTemplate<String, String> redisTemplate,
      JsonMapper jsonMapper) {
    this.redisTemplate = redisTemplate;
    this.jsonMapper = jsonMapper;
  }

  public Optional<List<MarketNewsCacheValue>> findLatestFive() {

    String json = redisTemplate.opsForValue().get(KEY_LATEST_FIVE);

    if (json == null) {
      return Optional.empty();
    }

    List<MarketNewsCacheValue> cachedNews = jsonMapper.readValue(
        json,
        new TypeReference<List<MarketNewsCacheValue>>() {}
    );

    return Optional.of(cachedNews);
  }

  public void save(List<MarketNewsCacheValue> latestNews) {

    String json = jsonMapper.writeValueAsString(latestNews);

    redisTemplate.opsForValue().set(
        KEY_LATEST_FIVE,
        json,
        TTL
    );
  }
}
