package com.lab.concurrencypalallelism.trasferManagement.infra.retry;

import com.lab.concurrencypalallelism.trasferManagement.application.command.NewTransaction;
import com.lab.concurrencypalallelism.trasferManagement.application.port.in.ProcessTransactionUseCase;
import com.lab.concurrencypalallelism.trasferManagement.application.result.ResultTransfer;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;

public class RetryProcessTransaction implements ProcessTransactionUseCase {


  private final ProcessTransactionUseCase delegate;

  public RetryProcessTransaction(ProcessTransactionUseCase delegate) {
    this.delegate = delegate;
  }

  @Override
  @Retryable(
      includes = ObjectOptimisticLockingFailureException.class,
      maxRetries = 3,
      delay = 100
  )
  public ResultTransfer proccess(NewTransaction newTransaction) {
    return delegate.proccess(newTransaction);
  }
}