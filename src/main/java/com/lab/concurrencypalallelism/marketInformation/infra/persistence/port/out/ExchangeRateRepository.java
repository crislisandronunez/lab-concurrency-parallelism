package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Currency;
import com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity.ExchangeRateEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, UUID> {


  @Query("""
    SELECT e FROM ExchangeRateEntity e
    WHERE (:fromCurrency IS NULL OR e.fromCurrency = :fromCurrency)
      AND (:toCurrency IS NULL OR e.toCurrency = :toCurrency)
    """)
  List<ExchangeRateEntity> findByCurrencies(
      @Param("fromCurrency") Currency fromCurrency,
      @Param("toCurrency") Currency toCurrency
  );



}
