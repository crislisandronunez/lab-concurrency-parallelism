package com.lab.concurrencypalallelism.trasferManagement.infra.persistence.entity;

import com.lab.concurrencypalallelism.trasferManagement.domain.enums.Category;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferStatus;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "transactions",
    indexes = {
        @Index(name = "idx_transactions_account_id", columnList = "account_id"),
        @Index(name = "idx_transactions_created_at", columnList = "created_at"),
        @Index(name = "idx_transactions_category", columnList = "category")
    }
)
public class TransactionEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "account_id", nullable = false)
  private UUID accountId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TransferType type;

  @Column(name = "amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TransferStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private Category category;

  protected TransactionEntity() {
  }

  public TransactionEntity(
      UUID id,
      UUID accountId,
      TransferType type,
      BigDecimal amount,
      Instant createdAt,
      TransferStatus status,
      Category category) {
    this.id = id;
    this.accountId = accountId;
    this.type = type;
    this.amount = amount;
    this.createdAt = createdAt;
    this.status = status;
    this.category = category;
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


}