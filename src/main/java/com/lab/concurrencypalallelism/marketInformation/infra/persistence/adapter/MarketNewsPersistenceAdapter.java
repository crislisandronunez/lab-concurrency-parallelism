package com.lab.concurrencypalallelism.marketInformation.infra.persistence.adapter;

import com.lab.concurrencypalallelism.marketInformation.application.port.out.MarketNewsPersistence;
import com.lab.concurrencypalallelism.marketInformation.domain.model.MarketNews;
import com.lab.concurrencypalallelism.marketInformation.infra.metrics.MarketCacheMetrics;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.mapper.MarketNewsMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.mapper.MarketNewsCacheMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.MarketNewsCacheValue;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.repository.MarketNewsCacheRepository;
import com.lab.concurrencypalallelism.marketInformation.infra.retry.RetryMarketNewsQuery;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class MarketNewsPersistenceAdapter implements MarketNewsPersistence {

  private static final Logger log = LoggerFactory.getLogger(MarketNewsPersistenceAdapter.class);

  private final RetryMarketNewsQuery retryMarketNewsQuery;
  private final MarketNewsCacheRepository marketNewsCacheRepository;
  private final MarketNewsMapper marketNewsMapper;
  private final MarketNewsCacheMapper marketNewsCacheMapper;
  private final MarketCacheMetrics marketCacheMetrics;

  public MarketNewsPersistenceAdapter(
      RetryMarketNewsQuery retryMarketNewsQuery,
      MarketNewsCacheRepository marketNewsCacheRepository,
      MarketNewsMapper marketNewsMapper,
      MarketNewsCacheMapper marketNewsCacheMapper,
      MarketCacheMetrics marketCacheMetrics) {
    this.retryMarketNewsQuery = retryMarketNewsQuery;
    this.marketNewsCacheRepository = marketNewsCacheRepository;
    this.marketNewsMapper = marketNewsMapper;
    this.marketNewsCacheMapper = marketNewsCacheMapper;
    this.marketCacheMetrics = marketCacheMetrics;
  }

  @Override
  public List<MarketNews> findLatestFive() {

    try {
      Optional<List<MarketNewsCacheValue>> cachedNews =
          marketNewsCacheRepository.findLatestFive();

      if (cachedNews.isPresent()) {
        marketCacheMetrics.recordHit("market_news");
        return cachedNews.get().stream()
            .map(marketNewsCacheMapper::toDomain)
            .toList();
      }

      marketCacheMetrics.recordMiss("market_news");

    } catch (RuntimeException exception) {
      log.warn("Error al consultar Redis", exception);
    }

    List<MarketNews> marketNewsList = retryMarketNewsQuery
        .findLatestFive()
        .stream()
        .map(marketNewsMapper::toDomain)
        .toList();

    List<MarketNewsCacheValue> marketNewsToCache = marketNewsList.stream()
        .map(marketNewsCacheMapper::toCacheValue)
        .toList();

    try {
      marketNewsCacheRepository.save(marketNewsToCache);
    } catch (RuntimeException exception) {
      log.warn("Error al guardar noticias en Redis", exception);
    }

    return marketNewsList;
  }
}
