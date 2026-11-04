package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.mapper;

import com.lab.concurrencypalallelism.marketInformation.domain.model.MarketNews;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.MarketNewsCacheValue;
import org.springframework.stereotype.Component;

@Component
public class MarketNewsCacheMapper {

  public MarketNewsCacheValue toCacheValue(MarketNews marketNews) {
    return new MarketNewsCacheValue(
        marketNews.getId(),
        marketNews.getTitle(),
        marketNews.getSource(),
        marketNews.getTimestamp(),
        marketNews.getSentiment()
    );
  }

  public MarketNews toDomain(MarketNewsCacheValue cachedNews) {
    return MarketNews.restore(
        cachedNews.id(),
        cachedNews.title(),
        cachedNews.source(),
        cachedNews.timestamp(),
        cachedNews.sentiment()
    );
  }
}
