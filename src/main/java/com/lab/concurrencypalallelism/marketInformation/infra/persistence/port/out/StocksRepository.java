package com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out;

import com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity.StocksEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StocksRepository extends JpaRepository<StocksEntity, String> {
}
