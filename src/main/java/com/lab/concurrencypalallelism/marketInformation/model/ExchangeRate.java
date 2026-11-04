package com.lab.concurrencypalallelism.marketInformation.model;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class ExchangeRate {

  private final Currency fromCurrency;
  private final Currency toCurrency;
  private BigDecimal rate;
  private Instant updateAt;

  private ExchangeRate(Currency fromCurrency, Currency toCurrency, BigDecimal rate, Instant updateAt) {
    this.fromCurrency = fromCurrency;
    this.toCurrency = toCurrency;
    this.rate = rate;
    this.updateAt = updateAt;
  }

  public static ExchangeRate create(Currency fromCurrency, Currency toCurrency, BigDecimal rate) {
    validateData(fromCurrency, toCurrency, rate);
    return new ExchangeRate(fromCurrency, toCurrency, rate, Instant.now());
  }

  public static ExchangeRate restore(
      Currency fromCurrency, Currency toCurrency, BigDecimal rate, Instant updateAt) {
    return new ExchangeRate(fromCurrency, toCurrency, rate, updateAt);
  }

  private static void validateData(Currency fromCurrency, Currency toCurrency, BigDecimal rate) {
    if (fromCurrency == null) {
      throw new IllegalArgumentException("La moneda de origen no puede ser nula");
    }
    if (toCurrency == null) {
      throw new IllegalArgumentException("La moneda de destino no puede ser nula");
    }
    if (fromCurrency == toCurrency) {
      throw new IllegalArgumentException("Las monedas de origen y destino deben ser diferentes");
    }
    if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("La tasa de cambio debe ser mayor que cero");
    }
  }

  public void update(BigDecimal rate) {
    if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("La tasa de cambio debe ser mayor que cero");
    }
    this.rate = rate;
    this.updateAt = Instant.now();
  }

  public Currency getFromCurrency() {
    return fromCurrency;
  }

  public Currency getToCurrency() {
    return toCurrency;
  }

  public BigDecimal getRate() {
    return rate;
  }

  public Instant getUpdateAt() {
    return updateAt;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ExchangeRate that)) return false;
    return fromCurrency == that.fromCurrency && toCurrency == that.toCurrency;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromCurrency, toCurrency);
  }
}
