package com.lab.concurrencypalallelism.trasferManagement.domain.model;

import com.lab.concurrencypalallelism.trasferManagement.domain.enums.AccountStatus;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.Currency;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Account {

  private final  UUID id;
  private final String holder;
  private BigDecimal balance;
  private final Currency currency;
  private AccountStatus status;

  private Account(UUID id, String holder, BigDecimal balance, Currency currency, AccountStatus status) {
    this.id = id;
    this.holder = holder;
    this.balance = balance;
    this.currency = currency;
    this.status = status;
  }

  public static Account create(String holder, BigDecimal balance, Currency currency) {
    validate(holder, balance, currency);
    return new Account(UUID.randomUUID(), holder, balance, currency, AccountStatus.ACTIVE);
  }

  private static void validate(String holder, BigDecimal balance, Currency currency) {
    if (holder == null || holder.isBlank()) {
      throw new IllegalArgumentException("El nombre del titular no puede ser nulo o vacío");
    }
    if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("El saldo no puede ser nulo o negativo");
    }
    if (currency == null ) {
      throw new IllegalArgumentException("La moneda no puede ser nula o vacía");
    }
  }

  public synchronized void updateBalance(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("El importe no puede ser nulo");
    }
    if (this.status != AccountStatus.ACTIVE) {
      throw new IllegalStateException("La cuenta debe estar activa para modificar el saldo");
    }
    BigDecimal newBalance = this.balance.add(amount);
    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("El saldo no puede quedar negativo");
    }
    this.balance = newBalance;
  }

  public synchronized void debitBalance(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("El importe no puede ser nulo");
    }
    if (this.status != AccountStatus.ACTIVE) {
      throw new IllegalStateException("La cuenta debe estar activa para modificar el saldo");
    }
    BigDecimal newBalance = this.balance.subtract(amount);
    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("El saldo no puede quedar negativo");
    }
    this.balance = newBalance;
  }

  public synchronized void activate() {
    if(this.status == AccountStatus.CLOSED) {
      throw new IllegalStateException("No se puede activar una cuenta cerrada");
    }
    this.status = AccountStatus.ACTIVE;

  }

  public synchronized void deactivate() {
    if(this.status == AccountStatus.CLOSED) {
      throw new IllegalStateException("No se puede desactivar una cuenta cerrada");
    }
    this.status = AccountStatus.INACTIVE;
  }

  public synchronized void close() {
    if(this.status == AccountStatus.CLOSED) {
      throw new IllegalStateException("La cuenta ya está cerrada");
    }
    this.status = AccountStatus.CLOSED;
  }

  public synchronized void suspend() {
    if(this.status == AccountStatus.CLOSED) {
      throw new IllegalStateException("No se puede suspender una cuenta cerrada");
    }
    this.status = AccountStatus.SUSPENDED;
  }

  public UUID getId() {
    return id;
  }

  public  synchronized BigDecimal getBalance() {
    return balance;
  }

  public Currency getCurrency() {
    return currency;
  }

  public  synchronized AccountStatus getStatus() {
    return status;
  }

  public String getHolder() {
    return holder;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Account account)) return false;
    return Objects.equals(id, account.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}

