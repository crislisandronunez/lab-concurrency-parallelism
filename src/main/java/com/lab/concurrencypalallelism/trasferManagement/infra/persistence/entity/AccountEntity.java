package com.lab.concurrencypalallelism.trasferManagement.infra.persistence.entity;

import com.lab.concurrencypalallelism.trasferManagement.domain.enums.AccountStatus;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;
  @Column(name = "holder", nullable = false)
  private String holder;
  @Column(name = "balance", nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;
  @Column(name = "currency", nullable = false)
  @Enumerated(EnumType.STRING)
  private  Currency currency;
  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private AccountStatus status;
  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  public AccountEntity() {
  }

  public AccountEntity(UUID id, String holder, BigDecimal balance, Currency currency, AccountStatus status, Long version) {
    this.id = id;
    this.holder = holder;
    this.balance = balance;
    this.currency = currency;
    this.status = status;
    this.version = version;
  }

  public UUID getId() {
    return id;
  }

  public String getHolder() {
    return holder;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public Currency getCurrency() {
    return currency;
  }

  public AccountStatus getStatus() {
    return status;
  }

  public Long getVersion() {
    return version;
  }
}
