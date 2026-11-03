package com.lab.concurrencypalallelism.trasferManagement.application.port.out;

import com.lab.concurrencypalallelism.trasferManagement.domain.model.Account;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

  Account save(Account account);

  Optional<Account> findById(UUID accountId);


}
