package com.lab.concurrencypalallelism.trasferManagement.infra.metrics;

import com.lab.concurrencypalallelism.trasferManagement.application.port.out.ProcessMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class ProcessTransactionMetrics implements ProcessMetrics {

  private final MeterRegistry meterRegistry;

  public ProcessTransactionMetrics(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public Timer.Sample start() {
    return Timer.start(meterRegistry);
  }

  @Override
  public void stop(Object sample, String method, String task) {
    ((Timer.Sample) sample).stop(
        Timer.builder("lab.execution.duration")
            .tag("method", method)
            .tag("task", task)
            .register(meterRegistry)
    );
  }
}



