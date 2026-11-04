package com.lab.concurrencypalallelism.marketInformation.infra.persistence.mapper;

import com.lab.concurrencypalallelism.marketInformation.domain.model.MarketNews;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity.MarketNewsEntity;
import org.springframework.stereotype.Component;

@Component
public class MarketNewsMapper {

  public MarketNewsEntity toEntity(MarketNews marketNews) {
    return new MarketNewsEntity(
        marketNews.getId(),
        marketNews.getTitle(),
        marketNews.getSource(),
        marketNews.getTimestamp(),
        marketNews.getSentiment()
    );
  }

  public MarketNews toDomain(MarketNewsEntity entity) {
    return MarketNews.restore(
        entity.getId(),
        entity.getTitle(),
        entity.getSource(),
        entity.getTimeStamp(),
        entity.getSentiment()
    );
  }
}
