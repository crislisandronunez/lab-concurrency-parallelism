package com.lab.concurrencypalallelism.trasferManagement.domain.model;

import com.lab.concurrencypalallelism.trasferManagement.domain.enums.Category;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferStatus;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

  private final UUID id;
  private final UUID accountId;
  private final TransferType type;
  private final BigDecimal amount;
  private final Instant createdAt;
  private TransferStatus status;
  private final Category category;

  private Transaction(UUID id, UUID accountId, TransferType type, BigDecimal amount, Instant createdAt, TransferStatus status, Category category) {
    this.id = id;
    this.accountId = accountId;
    this.type = type;
    this.amount = amount;
    this.createdAt = createdAt;
    this.status = status;
    this.category = category;
  }

  public  static Transaction create(UUID accountId, TransferType type, BigDecimal amount, Category category) {
    validate(amount, accountId, type, category);
    return new Transaction(
        UUID.randomUUID(),
        accountId,
        type,
        amount,
        Instant.now(),
        TransferStatus.PENDING,
        category);
  }

  private static void validate(BigDecimal amount, UUID accountId, TransferType type, Category category) {

    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El importe debe ser mayor a cero");
    }
    if(accountId == null) {
      throw new IllegalArgumentException("El id de la cuenta no puede ser nulo");
    }
    if(type == null) {
      throw new IllegalArgumentException("El tipo de transferencia no puede ser nulo");
    }
    if(category == null) {
      throw new IllegalArgumentException("La categoría no puede ser nula");
    }

  }

  public synchronized void complete() {
    if (this.status == TransferStatus.FAILED || this.status == TransferStatus.COMPLETED) {
      throw new IllegalArgumentException("El estado de la transacción no puede ser modificado a " + status);
    }
    this.status = TransferStatus.COMPLETED;
  }

  public synchronized   void fail() {
    if (this.status == TransferStatus.FAILED || this.status == TransferStatus.COMPLETED) {
      throw new IllegalArgumentException("El estado de la transacción no puede ser modificado a " + status);
    }
    this.status = TransferStatus.FAILED;
  }

  public UUID getId() {
    return id;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public TransferType getType() {
    return type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public TransferStatus getStatus() {
    return status;
  }

  public Category getCategory() {
    return category;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Transaction that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
