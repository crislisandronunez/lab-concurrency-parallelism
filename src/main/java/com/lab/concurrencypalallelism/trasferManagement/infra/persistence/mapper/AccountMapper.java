
package com.lab.concurrencypalallelism.trasferManagement.infra.persistence.mapper;

import com.lab.concurrencypalallelism.trasferManagement.domain.model.Account;
import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

  public AccountEntity toEntity(Account account) {
    return new AccountEntity(
        account.getId(),
        account.getHolder(),
        account.getBalance(),
        account.getCurrency(),
        account.getStatus()
    );
  }

  public Account toDomain(AccountEntity entity) {
    return Account.restore(
        entity.getId(),
        entity.getHolder(),
        entity.getBalance(),
        entity.getCurrency(),
        entity.getStatus()
    );
  }
}
