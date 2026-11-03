package com.lab.concurrencypalallelism.trasferManagement.infra.persistence.port.out;

import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.entity.AccountEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends
    JpaRepository<AccountEntity, UUID> {
}
