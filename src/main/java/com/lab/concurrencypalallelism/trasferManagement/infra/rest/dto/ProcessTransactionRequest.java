package com.lab.concurrencypalallelism.trasferManagement.infra.rest.dto;

import com.lab.concurrencypalallelism.trasferManagement.domain.enums.Category;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record ProcessTransactionRequest(
    @NotNull(message = "El identificador de la cuenta es obligatorio")
    UUID accountId,

    @NotNull(message = "El tipo de transacción es obligatorio")
    TransferType type,

    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El importe debe ser mayor a cero")
    BigDecimal amount,

    @NotNull(message = "La categoría es obligatoria")
    Category category
) {
}
