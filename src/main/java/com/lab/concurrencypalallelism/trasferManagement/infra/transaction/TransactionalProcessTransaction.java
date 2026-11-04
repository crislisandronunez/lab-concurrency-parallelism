package com.lab.concurrencypalallelism.trasferManagement.infra.transaction;

import com.lab.concurrencypalallelism.trasferManagement.application.command.NewTransaction;
import com.lab.concurrencypalallelism.trasferManagement.application.port.in.ProcessTransactionUseCase;
import com.lab.concurrencypalallelism.trasferManagement.application.result.ResultTransfer;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalProcessTransaction implements ProcessTransactionUseCase {

  private final ProcessTransactionUseCase delegate;

  public TransactionalProcessTransaction(ProcessTransactionUseCase delegate) {
    this.delegate = delegate;
  }

  @Override
  @Transactional
  public ResultTransfer proccess(NewTransaction newTransaction) {
    return delegate.proccess(newTransaction);
  }
}