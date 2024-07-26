package com.fiap.techchallenge.monitoring_consumer.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Log4j2
@Configuration
public class SchedulerConfig {

    @Value("${corePoolSize}")
    private int corePoolSize;

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        log.debug("Iniciando a pool de threads em {}", corePoolSize);
        return Executors.newScheduledThreadPool(corePoolSize); // Cria um pool de threads com 10 threads.
    }
}