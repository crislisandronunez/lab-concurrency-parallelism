package com.lab.concurrencypalallelism.marketInformation.application.port.out;

public interface MarketMetrics {

  Object start();

  void stop(Object sample, String method, String task);
}
