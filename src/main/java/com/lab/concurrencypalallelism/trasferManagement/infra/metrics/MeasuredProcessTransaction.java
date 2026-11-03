package com.lab.concurrencypalallelism.trasferManagement.infra.metrics;

import com.lab.concurrencypalallelism.trasferManagement.application.command.NewTransaction;
import com.lab.concurrencypalallelism.trasferManagement.application.port.in.ProcessTransactionUseCase;
import com.lab.concurrencypalallelism.trasferManagement.application.result.ResultTransfer;
import io.micrometer.core.instrument.Timer;

public class MeasuredProcessTransaction
  implements ProcessTransactionUseCase {


  private final ProcessTransactionUseCase delegate;
  private final ProcessTransactionMetrics metrics;

  public MeasuredProcessTransaction(
      ProcessTransactionUseCase delegate,
      ProcessTransactionMetrics metrics) {
    this.delegate = delegate;
    this.metrics = metrics;
  }


  @Override
  public ResultTransfer proccess(NewTransaction newTransaction) {

    Timer.Sample sample = metrics.start();

    try {
      return delegate.proccess(newTransaction);
    } finally {
      metrics.stop(sample, "transaction", "total");
    }
  }


}
