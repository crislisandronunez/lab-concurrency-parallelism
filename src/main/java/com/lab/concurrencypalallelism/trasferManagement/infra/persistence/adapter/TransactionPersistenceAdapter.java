package com.lab.concurrencypalallelism.trasferManagement.infra.persistence.adapter;

import com.lab.concurrencypalallelism.trasferManagement.application.port.out.TransactionPersistence;
import com.lab.concurrencypalallelism.trasferManagement.domain.model.Transaction;
import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.entity.TransactionEntity;
import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.mapper.TransactionMapper;
import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.port.out.TransactionRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionPersistenceAdapter implements TransactionPersistence {

  private final TransactionRepository repository;
  private final TransactionMapper mapper;

  public TransactionPersistenceAdapter(
      TransactionRepository repository,
      TransactionMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Transaction save(Transaction transaction) {
    TransactionEntity entity = mapper.toEntity(transaction);
    TransactionEntity savedEntity = repository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Transaction findById(UUID transactionId) {
    TransactionEntity entity = repository.findById(transactionId)
        .orElseThrow(() -> new IllegalArgumentException("La transacción no existe"));

    return mapper.toDomain(entity);
  }
}