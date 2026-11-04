package com.lab.concurrencypalallelism.marketInformation.application.port.out;

import com.lab.concurrencypalallelism.marketInformation.domain.model.Stocks;
import java.util.Optional;

public interface StocksPersistence {

  Optional<Stocks> findBySymbol(String symbol);
}
