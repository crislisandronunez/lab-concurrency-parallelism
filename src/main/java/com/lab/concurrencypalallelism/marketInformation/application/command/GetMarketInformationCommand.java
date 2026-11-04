
package com.lab.concurrencypalallelism.marketInformation.application.command;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;

public record GetMarketInformationCommand(
    String symbol,
    Currency fromCurrency,
    Currency toCurrency
) {
  public GetMarketInformationCommand {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("El símbolo no puede ser nulo o vacío");
    }
    if (symbol.length() > 10) {
      throw new IllegalArgumentException("El símbolo no puede superar los 10 caracteres");
    }
    if (fromCurrency == null && toCurrency == null) {
      throw new IllegalArgumentException("Debe indicar al menos una moneda para consultar el tipo de cambio");
    }
  }
}
