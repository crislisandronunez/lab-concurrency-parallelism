
package com.lab.concurrencypalallelism.marketInformation.application.usecase;

import com.lab.concurrencypalallelism.marketInformation.application.command.GetMarketInformationCommand;
import com.lab.concurrencypalallelism.marketInformation.application.port.in.MarketInformation;
import com.lab.concurrencypalallelism.marketInformation.application.port.out.ExchangeRatePersistence;
import com.lab.concurrencypalallelism.marketInformation.application.port.out.MarketMetrics;
import com.lab.concurrencypalallelism.marketInformation.application.port.out.MarketNewsPersistence;
import com.lab.concurrencypalallelism.marketInformation.application.port.out.StocksPersistence;
import com.lab.concurrencypalallelism.marketInformation.application.result.ExchangeRateResult;
import com.lab.concurrencypalallelism.marketInformation.application.result.MarketInformationResult;
import com.lab.concurrencypalallelism.marketInformation.application.result.MarketNewsResult;
import com.lab.concurrencypalallelism.marketInformation.application.result.StockResult;
import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import com.lab.concurrencypalallelism.marketInformation.domain.model.ExchangeRate;
import com.lab.concurrencypalallelism.marketInformation.domain.model.MarketNews;
import com.lab.concurrencypalallelism.marketInformation.domain.model.Stocks;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class CreateMarketInformation implements MarketInformation {

  private final StocksPersistence stocksPersistence;
  private final ExchangeRatePersistence exchangeRatePersistence;
  private final MarketNewsPersistence marketNewsPersistence;
  private final MarketMetrics marketMetrics;
  private final Executor executor;

  public CreateMarketInformation(
      StocksPersistence stocksPersistence,
      ExchangeRatePersistence exchangeRatePersistence,
      MarketNewsPersistence marketNewsPersistence,
      MarketMetrics marketMetrics,
      Executor executor) {
    this.stocksPersistence = stocksPersistence;
    this.exchangeRatePersistence = exchangeRatePersistence;
    this.marketNewsPersistence = marketNewsPersistence;
    this.marketMetrics = marketMetrics;
    this.executor = executor;
  }

  @Override
  public MarketInformationResult getMarketInformation(GetMarketInformationCommand command) {

    Object getMarketInformationSample = marketMetrics.start();

    StockResult stockResult;

    List<ExchangeRateResult> exchangeRateResultList;

    List<MarketNewsResult> marketNewsResultList;

    try {

      CompletableFuture<StockResult> stockResultCompletableFuture =
          CompletableFuture.supplyAsync(
              () -> getStock(command.symbol()),
              executor
          );

      CompletableFuture<List<ExchangeRateResult>> exchangeRateResultCompletableFuture =
          CompletableFuture.supplyAsync(
              () -> getExchangeRate(command.fromCurrency(), command.toCurrency()),
              executor
          );

      CompletableFuture<List<MarketNewsResult>> marketNewsResultCompleteFuture =
          CompletableFuture.supplyAsync(
              () -> getMarketNews(),
              executor
          );

      CompletableFuture.allOf(
          stockResultCompletableFuture,
          exchangeRateResultCompletableFuture,
          marketNewsResultCompleteFuture
      ).orTimeout(5, TimeUnit.SECONDS).join();

      stockResult = stockResultCompletableFuture.join();
      exchangeRateResultList = exchangeRateResultCompletableFuture.join();
      marketNewsResultList = marketNewsResultCompleteFuture.join();

    } finally {
      marketMetrics.stop(
          getMarketInformationSample,
          "getMarketinformation",
          "completable_future"
      );
    }

    return new MarketInformationResult(
        stockResult,
        exchangeRateResultList,
        marketNewsResultList
    );
  }

  private StockResult getStock(String symbol) {

    Object findStockSample = marketMetrics.start();

    Stocks stock;

    try {

      stock = stocksPersistence.findBySymbol(symbol)
          .orElseThrow(() -> new IllegalArgumentException(
              "No se encontró la acción con el símbolo: " + symbol
          ));
    } finally {
      marketMetrics.stop(
          findStockSample,
          "getStock",
          "find_stock"
      );
    }

    return new StockResult(
        stock.getSymbol(),
        stock.getPrice(),
        stock.getChangePercent(),
        stock.getVolume(),
        stock.getUpdateAt()
    );
  }

  private List<ExchangeRateResult> getExchangeRate(Currency currency1, Currency currency2) {

    Object findExchangeRateSample = marketMetrics.start();

    List<ExchangeRate> exchangeRatetList;

    try {
      exchangeRatetList = exchangeRatePersistence.findByCurrencies(currency1, currency2);

    } finally {
      marketMetrics.stop(
          findExchangeRateSample,
          "getExchangeRate",
          "find_by_currencies"
      );
    }

    List<ExchangeRateResult> exchangeRateResultList = new ArrayList<>();

    for (ExchangeRate exchangeRate : exchangeRatetList) {
      exchangeRateResultList.add(new ExchangeRateResult(
          exchangeRate.getFromCurrency(),
          exchangeRate.getToCurrency(),
          exchangeRate.getRate(),
          exchangeRate.getUpdateAt()
      ));
    }

    return exchangeRateResultList;
  }

  private List<MarketNewsResult> getMarketNews() {

    Object findByMarketNewSample = marketMetrics.start();

    List<MarketNews> marketNewsList;

    try {
      marketNewsList = marketNewsPersistence.findLatestFive();
    } finally {
      marketMetrics.stop(
          findByMarketNewSample,
          "getMarketNews",
          "find_latest_five"
      );
    }

    List<MarketNewsResult> marketNewsResultList = new ArrayList<>();

    for (MarketNews marketNews : marketNewsList) {
      marketNewsResultList.add(new MarketNewsResult(
          marketNews.getTitle(),
          marketNews.getSource(),
          marketNews.getTimestamp(),
          marketNews.getSentiment()
      ));
    }

    return marketNewsResultList;
  }
}
