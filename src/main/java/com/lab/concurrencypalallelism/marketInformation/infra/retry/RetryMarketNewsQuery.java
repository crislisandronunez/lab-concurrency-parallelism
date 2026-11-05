
package com.lab.concurrencypalallelism.marketInformation.infra.retry;

import com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity.MarketNewsEntity;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.postgres.MarketNewsRepository;
import java.util.List;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class RetryMarketNewsQuery {

  private final MarketNewsRepository marketNewsRepository;

  public RetryMarketNewsQuery(MarketNewsRepository marketNewsRepository) {
    this.marketNewsRepository = marketNewsRepository;
  }

  @Retryable(
      includes = TransientDataAccessException.class,
      maxRetries = 3,
      delay = 100
  )
  public List<MarketNewsEntity> findLatestFive() {
    return marketNewsRepository.findTop5ByOrderByTimeStampDesc();
  }
}
