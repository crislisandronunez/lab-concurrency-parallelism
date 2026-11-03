
package com.lab.concurrencypalallelism.trasferManagement.infra.rest.controller;

import com.lab.concurrencypalallelism.trasferManagement.application.command.NewTransaction;
import com.lab.concurrencypalallelism.trasferManagement.application.port.in.ProcessTransactionUseCase;
import com.lab.concurrencypalallelism.trasferManagement.application.result.ResultTransfer;
import com.lab.concurrencypalallelism.trasferManagement.infra.rest.dto.ProcessTransactionRequest;
import com.lab.concurrencypalallelism.trasferManagement.infra.rest.dto.ProcessTransactionResponse;
import com.lab.concurrencypalallelism.trasferManagement.infra.rest.mapper.ProcessTransactionMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class ProcessTransactionController {

  private final ProcessTransactionUseCase proccessTransactionUseCase;
  private final ProcessTransactionMapper mapper;

  public ProcessTransactionController(
      ProcessTransactionUseCase proccessTransactionUseCase,
      ProcessTransactionMapper mapper) {
    this.proccessTransactionUseCase = proccessTransactionUseCase;
    this.mapper = mapper;
  }

  @PostMapping
  public ResponseEntity<ProcessTransactionResponse> process(
      @Valid @RequestBody ProcessTransactionRequest request) {

    NewTransaction command = mapper.toCommand(request);

    ResultTransfer result = proccessTransactionUseCase.proccess(command);

    ProcessTransactionResponse response = mapper.toResponse(result);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
