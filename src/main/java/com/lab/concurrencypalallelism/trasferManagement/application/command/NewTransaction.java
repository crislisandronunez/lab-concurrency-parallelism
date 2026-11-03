package com.lab.concurrencypalallelism.trasferManagement.application.command;

import com.lab.concurrencypalallelism.trasferManagement.domain.enums.Category;
import com.lab.concurrencypalallelism.trasferManagement.domain.enums.TransferType;
import java.math.BigDecimal;
import java.util.UUID;

public record NewTransaction(
    UUID accountId,
    TransferType type,
    BigDecimal amount,
    Category category
) {
  public NewTransaction {

    if (accountId == null) {
      throw new IllegalArgumentException("El id de la cuenta no puede ser nulo");
    }

    if (type == null) {
      throw new IllegalArgumentException("El tipo de transferencia no puede ser nulo");
    }

    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El importe debe ser mayor a cero");
    }

    if (category == null) {
      throw new IllegalArgumentException("La categoría no puede ser nula");
    }
  }

}
