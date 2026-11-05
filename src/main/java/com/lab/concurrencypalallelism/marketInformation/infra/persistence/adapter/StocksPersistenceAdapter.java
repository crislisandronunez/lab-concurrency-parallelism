package com.lab.concurrencypalallelism.marketInformation.infra.persistence.adapter;

import com.lab.concurrencypalallelism.marketInformation.application.port.out.StocksPersistence;
import com.lab.concurrencypalallelism.marketInformation.domain.model.Stocks;
import com.lab.concurrencypalallelism.marketInformation.infra.metrics.MarketCacheMetrics;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.mapper.StocksMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.mapper.StocksCacheMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.StockCacheValue;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.repository.StocksCacheRepository;
import com.lab.concurrencypalallelism.marketInformation.infra.retry.RetryStocksQuery;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class StocksPersistenceAdapter implements StocksPersistence {

  private static final Logger log = LoggerFactory.getLogger(StocksPersistenceAdapter.class);

  private final RetryStocksQuery retryStocksQuery;
  private final StocksCacheRepository stocksCacheRepository;
  private final StocksMapper stocksMapper;
  private final StocksCacheMapper stocksCacheMapper;
  private final MarketCacheMetrics marketCacheMetrics;

  public StocksPersistenceAdapter(
      RetryStocksQuery retryStocksQuery,
      StocksCacheRepository stocksCacheRepository,
      StocksMapper stocksMapper,
      StocksCacheMapper stocksCacheMapper,
      MarketCacheMetrics marketCacheMetrics) {
    this.retryStocksQuery = retryStocksQuery;
    this.stocksCacheRepository = stocksCacheRepository;
    this.stocksMapper = stocksMapper;
    this.stocksCacheMapper = stocksCacheMapper;
    this.marketCacheMetrics = marketCacheMetrics;
  }

  @Override
  public Optional<Stocks> findBySymbol(String symbol) {

    try {
      Optional<StockCacheValue> cached = stocksCacheRepository.findBySymbol(symbol);

      if (cached.isPresent()) {
        marketCacheMetrics.recordHit("stocks");
        return cached.map(stocksCacheMapper::toDomain);
      }

      marketCacheMetrics.recordMiss("stocks");

    } catch (RuntimeException exception) {
      log.warn("Error consulta Redis");
    }

    Optional<Stocks> stock = retryStocksQuery.findById(symbol)
        .map(stocksMapper::toDomain);

    try {

      stock.ifPresent(stockToCache ->
          stocksCacheRepository.save(stocksCacheMapper.toCacheValue(stockToCache))
      );

    } catch (RuntimeException exception) {
      log.warn("Error de guardado en redis");
    }

    return stock;
  }
}
