package com.fiap.techchallenge.monitoring_consumer.client;

import com.fiap.techchallenge.monitoring_consumer.model.ParkingSessionModel;
import com.fiap.techchallenge.monitoring_consumer.model.PaymentResume;
import com.fiap.techchallenge.monitoring_consumer.request.RegisterPaymentRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class PaymentClient {

    private final WebClient.Builder webClientBuilder;
    private final DiscoveryClient discoveryClient;

    public PaymentClient(WebClient.Builder webClientBuilder, DiscoveryClient discoveryClient) {
        this.webClientBuilder = webClientBuilder;
        this.discoveryClient = discoveryClient;
    }


    public void registerPayment(RegisterPaymentRequest paymentRequest){
        String serviceUrl = discoveryClient.getInstances("payment-service")
                .stream()
                .findFirst()
                .map(si -> si.getUri().toString())
                .orElseThrow(() -> new IllegalStateException("Service not found"));

        Mono<String> stringMono = webClientBuilder.build()
                .post()
                .uri(serviceUrl + "/register-payment", "")
                .bodyValue(paymentRequest)
                .retrieve()
                .bodyToMono(String.class);
        //!necessário para a chamada assincrona ocorrer de fato
        stringMono.subscribe(
                result -> log.info("Response: {}", result),
                error -> log.error("Error: {}", error));

    }

    public PaymentResume getPaymentResume(String paymentId){
        String serviceUrl = discoveryClient.getInstances("payment-service")
                .stream()
                .findFirst()
                .map(si -> si.getUri().toString())
                .orElseThrow(() -> new IllegalStateException("Service not found"));

        return webClientBuilder.build()
                .get()
                .uri(serviceUrl + "/payment-resume/{paymentId}", paymentId)
                .retrieve()
                .bodyToMono(PaymentResume.class).block();
    }

}
