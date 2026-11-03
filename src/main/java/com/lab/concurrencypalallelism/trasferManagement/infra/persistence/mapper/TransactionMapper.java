package com.lab.concurrencypalallelism.trasferManagement.infra.persistence.mapper;

import com.lab.concurrencypalallelism.trasferManagement.domain.model.Transaction;
import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

  public TransactionEntity toEntity(Transaction transaction) {
    return new TransactionEntity(
        transaction.getId(),
        transaction.getAccountId(),
        transaction.getType(),
        transaction.getAmount(),
        transaction.getCreatedAt(),
        transaction.getStatus(),
        transaction.getCategory()
    );
  }

  public Transaction toDomain(TransactionEntity entity) {
    return Transaction.restore(
        entity.getId(),
        entity.getAccountId(),
        entity.getType(),
        entity.getAmount(),
        entity.getCreatedAt(),
        entity.getStatus(),
        entity.getCategory()
    );
  }
}