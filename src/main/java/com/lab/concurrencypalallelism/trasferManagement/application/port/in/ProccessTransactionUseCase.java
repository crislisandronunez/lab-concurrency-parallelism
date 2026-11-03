package com.lab.concurrencypalallelism.trasferManagement.application.port.in;

import com.lab.concurrencypalallelism.trasferManagement.application.command.NewTransaction;
import com.lab.concurrencypalallelism.trasferManagement.application.result.ResultTransfer;

public interface ProccessTransactionUseCase {


  ResultTransfer proccess(NewTransaction newTransaction);


}
