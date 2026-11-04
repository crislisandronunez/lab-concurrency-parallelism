package com.lab.concurrencypalallelism.marketInformation.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

public class Stocks {

  private final String symbol;
  private BigDecimal price;
  private BigDecimal changePercent;
  private Long volume;
  private Instant updateAt;

  private Stocks(String symbol, BigDecimal price, BigDecimal changePercent, Long volume, Instant updateAt) {
    this.symbol = symbol;
    this.price = price;
    this.changePercent = changePercent;
    this.volume = volume;
    this.updateAt = updateAt;
  }

  public static Stocks create(String symbol, BigDecimal price, BigDecimal changePercent, Long volume) {
    String normalizedSymbol = normalizeSymbol(symbol);
    validateData(symbol, price, changePercent, volume);
    return new Stocks(normalizedSymbol, price, changePercent, volume, Instant.now());
  }

  public static Stocks restore(String symbol, BigDecimal price, BigDecimal changePercent, Long volume, Instant updateAt) {
    return new Stocks(symbol, price, changePercent, volume, updateAt);
  }

  private static void validateData(String symbol, BigDecimal price, BigDecimal changePercent, Long volume) {

    if (symbol == null || symbol.isBlank() ) {
      throw new IllegalArgumentException("El símbolo no puede ser nulo o vacío");
    }
    if(symbol.length()>10){
      throw new IllegalArgumentException("El símbolo no puede tener más de 10 caracteres");
    }
    if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("El precio no puede ser nulo o negativo");
    }
    if (changePercent == null) {
      throw new IllegalArgumentException("El cambio porcentual no puede ser nulo");
    }
    if (volume == null || volume < 0) {
      throw new IllegalArgumentException("El volumen no puede ser nulo o negativo");
    }
  }

  private static String normalizeSymbol(String symbol) {
    return symbol.trim().toUpperCase(Locale.ROOT);
  }

  public void update(BigDecimal price, BigDecimal changePercent, Long volume) {
    if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("El precio no puede ser nulo o negativo");
    }
    if (changePercent == null) {
      throw new IllegalArgumentException("El cambio porcentual no puede ser nulo");
    }
    if (volume == null || volume < 0) {
      throw new IllegalArgumentException("El volumen no puede ser nulo o negativo");
    }
    this.price = price;
    this.changePercent = changePercent;
    this.volume = volume;
    this.updateAt = Instant.now();
  }

  public String getSymbol() {
    return symbol;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public BigDecimal getChangePercent() {
    return changePercent;
  }

  public Long getVolume() {
    return volume;
  }

  public Instant getUpdateAt() {
    return updateAt;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Stocks stocks)) return false;
    return Objects.equals(symbol, stocks.symbol);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(symbol);
  }
}
