package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.postgres;

import com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity.MarketNewsEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface MarketNewsRepository extends JpaRepository<MarketNewsEntity, UUID> {

  @Transactional(readOnly = true)
  List<MarketNewsEntity> findTop5ByOrderByTimeStampDesc();


}