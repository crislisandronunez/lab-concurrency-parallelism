package com.lab.concurrencypalallelism.marketInformation.infra.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MarketCacheMetrics {

  private final MeterRegistry meterRegistry;

  public MarketCacheMetrics(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  public void recordHit(String resource) {
    recordCacheRequest(resource, "hit");
  }

  public void recordMiss(String resource) {
    recordCacheRequest(resource, "miss");
  }

  private void recordCacheRequest(String resource, String result) {
    Counter.builder("lab.cache.requests")
        .tag("resource", resource)
        .tag("result", result)
        .register(meterRegistry)
        .increment();
  }
}
