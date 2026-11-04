package com.lab.concurrencypalallelism.marketInformation.application.port.in;

import com.lab.concurrencypalallelism.marketInformation.application.command.GetMarketInformationCommand;
import com.lab.concurrencypalallelism.marketInformation.application.result.MarketInformationResult;

public interface MarketInformation {

  MarketInformationResult getMarketInformation (GetMarketInformationCommand command);
}
