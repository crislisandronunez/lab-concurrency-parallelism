
package com.lab.concurrencypalallelism.marketInformation.infra.config;

import com.lab.concurrencypalallelism.marketInformation.application.port.out.ExchangeRatePersistence;
import com.lab.concurrencypalallelism.marketInformation.application.port.out.MarketMetrics;
import com.lab.concurrencypalallelism.marketInformation.application.port.out.MarketNewsPersistence;
import com.lab.concurrencypalallelism.marketInformation.application.port.out.StocksPersistence;
import com.lab.concurrencypalallelism.marketInformation.application.usecase.CreateMarketInformation;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.resilience.annotation.EnableResilientMethods;

@Configuration
@EnableResilientMethods
public class MarketInformationConfig {


  @Bean
  public CreateMarketInformation createMarketInformation(
      StocksPersistence stocksPersistence,
      ExchangeRatePersistence exchangeRatePersistence,
      MarketNewsPersistence marketNewsPersistence,
      MarketMetrics marketMetrics,
      @Qualifier("marketInformationExecutor") Executor executor) {

    return new CreateMarketInformation(
        stocksPersistence,
        exchangeRatePersistence,
        marketNewsPersistence,
        marketMetrics,
        executor
    );
  }
}
