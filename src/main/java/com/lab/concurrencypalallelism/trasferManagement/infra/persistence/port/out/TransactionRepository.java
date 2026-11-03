package com.lab.concurrencypalallelism.trasferManagement.infra.persistence.port.out;

import com.lab.concurrencypalallelism.trasferManagement.infra.persistence.entity.TransactionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

}
