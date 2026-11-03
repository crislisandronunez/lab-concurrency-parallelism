package com.lab.concurrencypalallelism.trasferManagement.infra.config;

import com.lab.concurrencypalallelism.trasferManagement.application.port.in.ProccessTransactionUseCase;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.AccountPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.TransactionPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.usecase.ProccessTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessTransferConfig {

  @Bean
  public ProccessTransactionUseCase proccessTransaction(
      TransactionPersistence transactionRepository,
      AccountPersistence accountRepository) {

    return new ProccessTransaction(transactionRepository, accountRepository);
  }
}

