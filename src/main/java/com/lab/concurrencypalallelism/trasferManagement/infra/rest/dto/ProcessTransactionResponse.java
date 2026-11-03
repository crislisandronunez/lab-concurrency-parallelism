package com.lab.concurrencypalallelism.trasferManagement.infra.rest.dto;

import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferStatus;
import java.util.UUID;

public record ProcessTransactionResponse(

    UUID id,
    TransferStatus status,
    String message
) {
}
