
package com.lab.concurrencypalallelism.marketInformation.infra.rest.dto;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GetMarketInformationRequest(

    @NotBlank(message = "El símbolo es obligatorio")
    @Size(max = 10, message = "El símbolo no puede superar los 10 caracteres")
    String symbol,

    Currency fromCurrency,

    Currency toCurrency
) {
}
