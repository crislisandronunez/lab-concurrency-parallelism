package com.lab.concurrencypalallelism.trasferManagement.infra.persistence.adapter;

import com.lab.concurrencypalallelism.trasferManagement.application.port.out.AccountPersistence;
import com.lab.concurrencypalallelism.trasferManagement.domain.model.Account;
import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.entity.AccountEntity;
import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.mapper.AccountMapper;
import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.port.out.AccountRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class AccountPersistenceAdapter implements AccountPersistence {

  private final AccountRepository repository;
  private final AccountMapper mapper;

  public AccountPersistenceAdapter(
      AccountRepository repository,
      AccountMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Account save(Account account) {
    AccountEntity entity = mapper.toEntity(account);
    AccountEntity savedEntity = repository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<Account> findById(UUID accountId) {
    return repository.findById(accountId)
        .map(mapper::toDomain);
  }
}
