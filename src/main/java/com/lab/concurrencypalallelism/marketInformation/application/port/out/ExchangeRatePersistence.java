package com.lab.concurrencypalallelism.marketInformation.application.port.out;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import com.lab.concurrencypalallelism.marketInformation.model.ExchangeRate;
import java.util.List;

public interface ExchangeRatePersistence {

  List<ExchangeRate> findByCurrencies(Currency fromCurrency, Currency toCurrency);

}
