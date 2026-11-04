package com.lab.concurrencypalallelism.trasferManagement.infra.config;

import com.lab.concurrencypalallelism.trasferManagement.application.port.in.ProcessTransactionUseCase;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.AccountPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.ProcessMetrics;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.TransactionPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.usecase.ProcessTransaction;
import com.lab.concurrencypalallelism.trasferManagement.infra.transaction.TransactionalProcessTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessTransferConfig {

  @Bean
  public ProcessTransactionUseCase proccessTransaction(
      TransactionPersistence transactionRepository,
      AccountPersistence accountRepository,
      ProcessMetrics processMetrics
  ) {

    ProcessTransaction processTransaction = new ProcessTransaction(
        transactionRepository,
        accountRepository,
        processMetrics
    );

    return new TransactionalProcessTransaction(processTransaction);
  }
}