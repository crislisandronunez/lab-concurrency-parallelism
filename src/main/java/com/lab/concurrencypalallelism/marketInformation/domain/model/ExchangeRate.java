package com.lab.concurrencypalallelism.marketInformation.domain.model;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ExchangeRate {

  private final UUID id;
  private final Currency fromCurrency;
  private final Currency toCurrency;
  private BigDecimal rate;
  private Instant updateAt;

  private ExchangeRate(UUID id, Currency fromCurrency, Currency toCurrency,
                       BigDecimal rate, Instant updateAt) {
    this.id = id;
    this.fromCurrency = fromCurrency;
    this.toCurrency = toCurrency;
    this.rate = rate;
    this.updateAt = updateAt;
  }

  public static ExchangeRate create(Currency fromCurrency, Currency toCurrency, BigDecimal rate) {
    validateData(fromCurrency, toCurrency, rate);
    return new ExchangeRate(UUID.randomUUID(), fromCurrency, toCurrency, rate, Instant.now());
  }

  public static ExchangeRate restore(
      UUID id, Currency fromCurrency, Currency toCurrency, BigDecimal rate, Instant updateAt) {
    return new ExchangeRate(id, fromCurrency, toCurrency, rate, updateAt);
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

  public UUID getId() {
    return id;
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
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
