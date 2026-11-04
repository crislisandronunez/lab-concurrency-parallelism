
package com.lab.concurrencypalallelism.trasferManagement.application.usecase;

import com.lab.concurrencypalallelism.trasferManagement.application.command.NewTransaction;
import com.lab.concurrencypalallelism.trasferManagement.application.port.in.ProcessTransactionUseCase;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.AccountPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.ProcessMetrics;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.TransactionPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.result.ResultTransfer;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.AccountStatus;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferType;
import com.lab.concurrencypalallelism.trasferManagement.domain.model.Account;
import com.lab.concurrencypalallelism.trasferManagement.domain.model.Transaction;
import java.util.Objects;

public class ProcessTransaction implements ProcessTransactionUseCase {

  private final TransactionPersistence transactionRepository;
  private final AccountPersistence accountRepository;
  private final ProcessMetrics metrics;

  public ProcessTransaction(
      TransactionPersistence transactionRepository,
      AccountPersistence accountRepository,
      ProcessMetrics metrics) {
    this.transactionRepository = Objects.requireNonNull(transactionRepository);
    this.accountRepository = Objects.requireNonNull(accountRepository);
    this.metrics = metrics;
  }

  @Override
  public ResultTransfer proccess(NewTransaction newTransaction) {

    Object findAccountSample = metrics.start();
    Account account;

    try {
      account = accountRepository.findById(newTransaction.accountId())
          .orElseThrow(() -> new IllegalArgumentException("La cuenta no existe"));
    } finally {
      metrics.stop(findAccountSample, "transaction", "find_account");
    }


    if (account.getStatus() != AccountStatus.ACTIVE) {
      throw new IllegalArgumentException("La cuenta no está activa");
    }

    if (newTransaction.type() != TransferType.DEBIT) {
      throw new IllegalArgumentException("Solo se permiten operaciones de débito");
    }

    if (account.getBalance().compareTo(newTransaction.amount()) < 0) {
      throw new IllegalArgumentException("No hay suficiente saldo en la cuenta");
    }

    account.debitBalance(newTransaction.amount());

    Object saveAccountSample = metrics.start();
    Account updatedAccount;

    try {
      updatedAccount = accountRepository.save(account);
    } finally {
      metrics.stop(saveAccountSample, "transaction", "save_account");
    }


    Transaction transaction = Transaction.create(
        updatedAccount.getId(),
        newTransaction.type(),
        newTransaction.amount(),
        newTransaction.category()
    );

    transaction.complete();

    Object saveTransactionSample = metrics.start();
    Transaction savedTransaction;

    try {
      savedTransaction = transactionRepository.save(transaction);
    } finally {
      metrics.stop(saveTransactionSample, "transaction", "save_transaction");
    }

    return new ResultTransfer(
        savedTransaction.getId(),
        savedTransaction.getStatus(),
        "La transacción se ha procesado correctamente"
    );
  }
}
