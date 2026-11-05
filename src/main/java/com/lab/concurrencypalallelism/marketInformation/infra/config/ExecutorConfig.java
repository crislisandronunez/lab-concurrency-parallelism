package com.lab.concurrencypalallelism.marketInformation.infra.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

  @Bean(name = "marketInformationExecutor", destroyMethod = "shutdown")
  public ExecutorService marketInformationExecutor() {
    return Executors.newFixedThreadPool(3);
  }

}
