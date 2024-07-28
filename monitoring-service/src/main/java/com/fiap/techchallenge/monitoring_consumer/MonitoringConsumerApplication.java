package com.fiap.techchallenge.monitoring_consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MonitoringConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitoringConsumerApplication.class, args);
	}

}
