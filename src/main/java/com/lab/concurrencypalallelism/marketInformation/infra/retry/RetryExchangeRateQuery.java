package com.lab.concurrencypalallelism.marketInformation.infra.retry;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity.ExchangeRateEntity;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.postgres.ExchangeRateRepository;
import java.util.List;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class RetryExchangeRateQuery {

  private final ExchangeRateRepository exchangeRateRepository;

  public RetryExchangeRateQuery(ExchangeRateRepository exchangeRateRepository) {
    this.exchangeRateRepository = exchangeRateRepository;
  }

  @Retryable(
      includes = TransientDataAccessException.class,
      maxRetries = 3,
      delay = 100
  )
  public List<ExchangeRateEntity> findByCurrencies(
      Currency fromCurrency,
      Currency toCurrency) {
    return exchangeRateRepository.findByCurrencies(fromCurrency, toCurrency);
  }
}
