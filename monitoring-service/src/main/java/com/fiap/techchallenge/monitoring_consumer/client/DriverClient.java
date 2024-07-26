package com.fiap.techchallenge.monitoring_consumer.client;

import com.fiap.techchallenge.monitoring_consumer.model.Driver;
import com.fiap.techchallenge.monitoring_consumer.model.ParkingSessionModel;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class DriverClient {

    private final WebClient.Builder webClientBuilder;
    private final DiscoveryClient discoveryClient;

    public DriverClient(WebClient.Builder webClientBuilder, DiscoveryClient discoveryClient) {
        this.webClientBuilder = webClientBuilder;
        this.discoveryClient = discoveryClient;
    }


    public Driver getDriver(String driverId){
        String serviceUrl = discoveryClient.getInstances("driver-service")
                .stream()
                .findFirst()
                .map(si -> si.getUri().toString())
                .orElseThrow(() -> new IllegalStateException("Service not found"));

        return webClientBuilder.build()
                .get()
                .uri(serviceUrl + "/drivers/{driverId}", driverId)
                //.bodyValue(parkingSessionModel)
                .retrieve()
                .bodyToMono(Driver.class)
                .block();
    }

}
