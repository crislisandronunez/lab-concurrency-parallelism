package com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "exchange_rates",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_exchange_rates_currency_pair",
            columnNames = {"from_currency", "to_currency"}
        )
    }
)
public class ExchangeRateEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "from_currency", nullable = false)
  @Enumerated(EnumType.STRING)
  private Currency fromCurrency;

  @Column(name = "to_currency", nullable = false)
  @Enumerated(EnumType.STRING)
  private Currency toCurrency;

  @Column(name = "rate", nullable = false, precision = 19, scale = 2)
  private BigDecimal rate;

  @Column(name = "update_at", nullable = false)
  private Instant updateAt;

  protected ExchangeRateEntity() {
  }

  public ExchangeRateEntity(
      UUID id,
      Currency fromCurrency,
      Currency toCurrency,
      BigDecimal rate,
      Instant updateAt) {
    this.id = id;
    this.fromCurrency = fromCurrency;
    this.toCurrency = toCurrency;
    this.rate = rate;
    this.updateAt = updateAt;
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
    if (!(o instanceof ExchangeRateEntity that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
