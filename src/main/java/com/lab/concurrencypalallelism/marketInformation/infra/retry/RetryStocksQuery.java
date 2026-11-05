package com.lab.concurrencypalallelism.marketInformation.infra.retry;

import com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity.StocksEntity;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.postgres.StocksRepository;
import java.util.Optional;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class RetryStocksQuery {

  private final StocksRepository stocksRepository;

  public RetryStocksQuery(StocksRepository stocksRepository) {
    this.stocksRepository = stocksRepository;
  }

  @Retryable(
      includes = TransientDataAccessException.class,
      maxRetries = 3,
      delay = 100
  )
  public Optional<StocksEntity> findById(String symbol) {
    return stocksRepository.findById(symbol);
  }
}
