package com.lab.concurrencypalallelism.trasferManagement.application.result;

import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferStatus;
import java.util.UUID;

public record ResultTransfer(
    UUID id,
    TransferStatus status,
    String message
    ) {
}
