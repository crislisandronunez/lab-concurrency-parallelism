package com.lab.concurrencypalallelism.marketInformation.application.port.out;

import com.lab.concurrencypalallelism.marketInformation.domain.model.MarketNews;
import java.util.List;

public interface MarketNewsPersistence {

  List<MarketNews> findLatestFive();


}
