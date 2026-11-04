
package com.lab.concurrencypalallelism.marketInformation.infra.persistence.adapter;

import com.lab.concurrencypalallelism.marketInformation.application.port.out.StocksPersistence;
import com.lab.concurrencypalallelism.marketInformation.domain.model.Stocks;
import com.lab.concurrencypalallelism.marketInformation.infra.metrics.MarketCacheMetrics;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.mapper.StocksMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.postgres.StocksRepository;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.mapper.StocksCacheMapper;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.model.StockCacheValue;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.redis.repository.StocksCacheRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class StocksPersistenceAdapter implements StocksPersistence {

  private final StocksRepository stocksRepository;
  private final StocksCacheRepository stocksCacheRepository;
  private final StocksMapper stocksMapper;
  private final StocksCacheMapper stocksCacheMapper;
  private final MarketCacheMetrics marketCacheMetrics;

  public StocksPersistenceAdapter(
      StocksRepository stocksRepository,
      StocksCacheRepository stocksCacheRepository,
      StocksMapper stocksMapper,
      StocksCacheMapper stocksCacheMapper,
      MarketCacheMetrics marketCacheMetrics) {
    this.stocksRepository = stocksRepository;
    this.stocksCacheRepository = stocksCacheRepository;
    this.stocksMapper = stocksMapper;
    this.stocksCacheMapper = stocksCacheMapper;
    this.marketCacheMetrics = marketCacheMetrics;
  }

  @Override
  public Optional<Stocks> findBySymbol(String symbol) {

    Optional<StockCacheValue> cached = stocksCacheRepository.findBySymbol(symbol);

    if (cached.isPresent()) {
      marketCacheMetrics.recordHit("stocks");
      return cached.map(stocksCacheMapper::toDomain);
    }

    marketCacheMetrics.recordMiss("stocks");

    Optional<Stocks> stock = stocksRepository.findById(symbol)
        .map(stocksMapper::toDomain);

    stock.ifPresent(stockToCache ->
        stocksCacheRepository.save(stocksCacheMapper.toCacheValue(stockToCache))
    );

    return stock;
  }
}
