package com.lab.concurrencypalallelism.trasferManagement.infra.config;

import com.lab.concurrencypalallelism.trasferManagement.application.port.in.ProcessTransactionUseCase;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.AccountPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.ProcessMetrics;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.TransactionPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.usecase.ProcessTransaction;
import com.lab.concurrencypalallelism.trasferManagement.infra.retry.RetryProcessTransaction;
import com.lab.concurrencypalallelism.trasferManagement.infra.transaction.TransactionalProcessTransaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.resilience.annotation.EnableResilientMethods;

@Configuration
@EnableResilientMethods
public class ProcessTransferConfig {

  @Bean
  public ProcessTransaction processTransaction(
      TransactionPersistence transactionRepository,
      AccountPersistence accountRepository,
      ProcessMetrics processMetrics
  ) {
    return new ProcessTransaction(
        transactionRepository,
        accountRepository,
        processMetrics
    );
  }

  @Bean
  public ProcessTransactionUseCase transactionalProcessTransaction(
      ProcessTransaction processTransaction
  ) {
    return new TransactionalProcessTransaction(processTransaction);
  }

  @Bean
  @Primary
  public ProcessTransactionUseCase retryProcessTransaction(
      @Qualifier("transactionalProcessTransaction")
      ProcessTransactionUseCase transactionalProcessTransaction
  ) {
    return new RetryProcessTransaction(transactionalProcessTransaction);
  }
}