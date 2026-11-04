package com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "stocks")
public class StocksEntity {

  @Id
  @Column(name = "symbol", nullable = false)
  private String symbol;

  @Column(name = "price", nullable = false, precision = 19, scale = 2)
  private BigDecimal price;

  @Column(name = "change_percent", nullable = false, precision = 5, scale = 2)
  private BigDecimal changePercent;

  @Column(name = "volume", nullable = false)
  private Long volume;

  @Column(name = "update_at", nullable = false)
  private Instant updateAt;

  protected StocksEntity() {
  }

  public StocksEntity(
      String symbol,
      BigDecimal price,
      BigDecimal changePercent,
      Long volume,
      Instant updateAt) {
    this.symbol = symbol;
    this.price = price;
    this.changePercent = changePercent;
    this.volume = volume;
    this.updateAt = updateAt;
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
    if (!(o instanceof StocksEntity that)) return false;
    return Objects.equals(symbol, that.symbol);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(symbol);
  }
}
