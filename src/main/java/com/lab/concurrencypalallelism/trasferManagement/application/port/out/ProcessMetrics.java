package com.lab.concurrencypalallelism.trasferManagement.application.port.out;

import io.micrometer.core.instrument.Timer;

public interface ProcessMetrics {

  Object start();

  void stop(Object sample,String method, String task);

  }
