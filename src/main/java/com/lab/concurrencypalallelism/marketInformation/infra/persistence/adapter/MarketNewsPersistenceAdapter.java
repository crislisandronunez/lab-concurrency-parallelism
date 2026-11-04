package com.lab.concurrencypalallelism.marketInformation.infra.persistence.adapter;

import com.lab.concurrencypalallelism.marketInformation.application.port.out.MarketNewsPersistence;
import com.lab.concurrencypalallelism.marketInformation.domain.model.MarketNews;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.mapper.MarketNewsMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.postgres.MarketNewsRepository;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.mapper.MarketNewsCacheMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.MarketNewsCacheValue;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.repository.MarketNewsCacheRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MarketNewsPersistenceAdapter implements MarketNewsPersistence {

  private final MarketNewsRepository marketNewsRepository;
  private final MarketNewsCacheRepository marketNewsCacheRepository;
  private final MarketNewsMapper marketNewsMapper;
  private final MarketNewsCacheMapper marketNewsCacheMapper;

  public MarketNewsPersistenceAdapter(
      MarketNewsRepository marketNewsRepository,
      MarketNewsCacheRepository marketNewsCacheRepository,
      MarketNewsMapper marketNewsMapper,
      MarketNewsCacheMapper marketNewsCacheMapper) {
    this.marketNewsRepository = marketNewsRepository;
    this.marketNewsCacheRepository = marketNewsCacheRepository;
    this.marketNewsMapper = marketNewsMapper;
    this.marketNewsCacheMapper = marketNewsCacheMapper;
  }

  @Override
  public List<MarketNews> findLatestFive() {

    Optional<List<MarketNewsCacheValue>> cachedNews =
        marketNewsCacheRepository.findLatestFive();

    if (cachedNews.isPresent()) {
      return cachedNews.get().stream()
          .map(marketNewsCacheMapper::toDomain)
          .toList();
    }

    List<MarketNews> marketNewsList = marketNewsRepository
        .findTop5ByOrderByTimeStampDesc()
        .stream()
        .map(marketNewsMapper::toDomain)
        .toList();

    List<MarketNewsCacheValue> marketNewsToCache = marketNewsList.stream()
        .map(marketNewsCacheMapper::toCacheValue)
        .toList();

    marketNewsCacheRepository.save(marketNewsToCache);

    return marketNewsList;
  }
}
