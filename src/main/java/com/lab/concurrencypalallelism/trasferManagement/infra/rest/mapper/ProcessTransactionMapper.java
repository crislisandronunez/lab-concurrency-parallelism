
package com.lab.concurrencypalallelism.trasferManagement.infra.rest.mapper;

import com.lab.concurrencypalallelism.trasferManagement.application.command.NewTransaction;
import com.lab.concurrencypalallelism.trasferManagement.application.result.ResultTransfer;
import com.lab.concurrencypalallelism.trasferManagement.infra.rest.dto.ProcessTransactionRequest;
import com.lab.concurrencypalallelism.trasferManagement.infra.rest.dto.ProcessTransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class ProcessTransactionMapper {

  public NewTransaction toCommand(ProcessTransactionRequest request) {
    return new NewTransaction(
        request.accountId(),
        request.type(),
        request.amount(),
        request.category()
    );
  }

  public ProcessTransactionResponse toResponse(ResultTransfer result) {
    return new ProcessTransactionResponse(
        result.id(),
        result.status(),
        result.message()
    );
  }
}
