package com.lab.concurrencypalallelism.trasferManagement.application.usecase;

import com.lab.concurrencypalallelism.trasferManagement.application.command.NewTransaction;
import com.lab.concurrencypalallelism.trasferManagement.application.port.in.ProccessTransactionUseCase;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.AccountPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.port.out.TransactionPersistence;
import com.lab.concurrencypalallelism.trasferManagement.application.result.ResultTransfer;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.AccountStatus;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferType;
import com.lab.concurrencypalallelism.trasferManagement.domain.model.Account;
import com.lab.concurrencypalallelism.trasferManagement.domain.model.Transaction;
import java.util.Objects;

public class ProccessTransaction implements
    ProccessTransactionUseCase {

  private final TransactionPersistence transactionRepository;
  private final AccountPersistence accountRepository;

  public ProccessTransaction(
      TransactionPersistence transactionRepository,
      AccountPersistence accountRepository) {
    this.transactionRepository = Objects.requireNonNull(transactionRepository);
    this.accountRepository = Objects.requireNonNull(accountRepository);
  }

  @Override
  public ResultTransfer proccess(NewTransaction newTransaction) {

    Account account = accountRepository.findById(newTransaction.accountId())
        .orElseThrow(() -> new IllegalArgumentException("La cuenta no existe"));

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

    Account updatedAccount = accountRepository.save(account);

    Transaction transaction = Transaction.create(
        updatedAccount.getId(),
        newTransaction.type(),
        newTransaction.amount(),
        newTransaction.category()
    );

    transaction.complete();

    Transaction savedTransaction = transactionRepository.save(transaction);

    return new ResultTransfer(
        savedTransaction.getId(),
        savedTransaction.getStatus(),
        "La transacción se ha procesado correctamente"
    );
  }
}
