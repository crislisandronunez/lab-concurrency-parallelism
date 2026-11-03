package com.lab.concurrencypalallelism.trasferManagement.application.port.out;

import com.lab.concurrencypalallelism.trasferManagement.domain.model.Transaction;
import java.util.UUID;

public interface TransactionPersistence {

  Transaction save(Transaction transaction);

  Transaction findById(UUID transactionId);



}
