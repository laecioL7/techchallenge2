package com.fiap.techchallenge.payment_service.service;

import com.fiap.techchallenge.payment_service.exceptions.ResourceNotFoundException;
import com.fiap.techchallenge.payment_service.model.*;
import com.fiap.techchallenge.payment_service.repository.PaymentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;

@Log4j2
@Service
public class PaymentService {

    @Value("${payment.price.by-hour}")
    double priceByHour;

    private final PaymentRepository paymentRepository;
    private final WebClient.Builder webClientBuilder;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, WebClient.Builder webClientBuilder, DiscoveryClient discoveryClient) {
        this.paymentRepository = paymentRepository;
        this.webClientBuilder = webClientBuilder;
        this.discoveryClient = discoveryClient;
    }


    public String registerPayment(ParkingSessionModel parkingSession){
        log.info("Salvando resumo de pagamento para a sessão de estacionamento do motorista {}" + parkingSession.getDriverId());
        PaymentResume resume = new PaymentResume();

        //chama driverService para pegar a forma de pagamento
        Driver driver = getDriver(parkingSession.getDriverId());

        resume.setPaymentMethod(driver.getPaymentMethod());
        resume.setTotalHours(calculateHours(parkingSession.getStartTime(), parkingSession.getEndTime()));
        resume.setTotalPayment(calculatePayment(resume.getTotalHours()));
        resume.setStatus(PaymentStatus.PENDENTE);
        resume.setDriver(driver);
        resume.setParkingSessionModel(parkingSession);

        return paymentRepository.save(resume).getId();
    }

    private long calculateHours(LocalDateTime start, LocalDateTime end) {
        // Calcular a diferença em minutos
        long minutesDifference = Duration.between(start, end).toMinutes();

        // Converte minutos em horas, arredondando para cima se for menor que 60 minutos
        long hoursDifference = (minutesDifference + 59) / 60;

        log.info("Calculo de horas de estacionamento: {}", hoursDifference);
        return hoursDifference;
    }

    private double calculatePayment(Long totalHours){
        double price = priceByHour;
        return totalHours * price;
    }

    public PaymentResume getPaymentResume(String paymentId){
        return paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resumo de pagamento não encontrato"));
    }

    public String gerarQRCode() {
        //TODO: integração real
        return "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAAAXNSR0IArs4c6QAABCpJREFUeF7tnMFSAzEMQ9n//+gyw162wxieImdrqDiHxJEsW04Lx+PxeHzkZwwCRwgZw8VXICFkFh8hZBgfISSETENgWDzpISFkGALDwolCQsgwBIaFE4WEkGEIDAsnCvkPhBzH0X4N542zikfdc8K9lhQyIfBrRoSQKARXCFmlK8/v14xUD6wy+7pPtT85l6yp0HR+l9yLsGiXrBDyHWaH2BBywdMBcqRCSLNXS9P1opUaSVNX11RxVmWH3Ov2khVCTsgdpbWWrBDyBoQ4pUN1U8TdvX3JCiGg8+zOpK79oxAwwRM3QgjpclxqPClZly9WEvfSZW+dUkniLNXb+XSyw2WRDFYfGlUFdt0LdIO172U5GUAm2h2A7S415F4hBCCgKhBsOWcwJMGSTIpCRCRJTRW3/CCZ6hCl7q/GT0oi2dN+OiGHkDUqYLvXk5jJGvXjiRBS2GoCNllzCyEkkN1r1PJFetfumMn+SwohG+9eE0J2IyzuH0IAYGRgVB0aebOa1uDVvvFUTleeTipuQsiJTAgBD5BqiVOVfE3S2wnpupxajiqn1DWUkf0J2KRSlFVmpWSFkJ8baggp8CHZTPqhWo5uJ4RcQv2AR720OugRVaulj+ypJkXrYEgCBO5ZdikkI53Y1F5H4mntIVHIiQAhebxCurKNgEHKoLoP2ZNUgTEKCSG/OLQV2+uUrBByIyGEKFXOhEBSRpzS5Oyv3rfVZYWQEwG1kT9Z+M6SFUKGEeL4byLt6sHPysjL11/JPuSOZM1LXRYBm6wJIQSlyxonM8hRIaRAiXxWoMq/IkT9NLByRGqyOHdUz7KbuhPs0+Eb/nwhhBSpHYWsOa6XziHkiZ4MhqSsqT1Kja2rv4UQYEoI2GQNSopXDoZqFlb95+0VQrJBfTtSew5ZrxoIksHENJC3r9bBMIR8b9gEE0L4Ug8hh0chg10WyQxSXojjIuVCHdxIApL+RnBYUgjZ2FnjABBCHOTFp5koRATM4YZYV6Ic8qxDnJIaD1lP8FkqWeqlSSDkQiEkCvlCgDhGklAkMW2FqAMacSMqAOWQJb4mq/s4d982GDpBqcB32VXixBwbTpQQQgoESEKR3uWQ8ET+yuMiyewqQKfWEjPhAExAJdabxHCLQlTAppWgEGJ8BYcoUG3SIWQzIU5pdQgnxL5lyQohou/f3UNCSBMhBEjy8S+ZN4ilJWalq8S1TuokcGJ7Q4jYhQhgJGPIPiqB5Fx1CieJpp57S1MXeUX/LFIljVhdMrh1nStj0jmpy4cD29sFjGMgiErVu0chgPyqlP0pQroyg5QR9SxS+4lbUwlR1dj6uKiC1NX8yLkhhKBUrIlCTmCW5hAD9/zqLwiEkGEpEkJCyDAEhoUThYSQYQgMCycKCSHDEBgWThQSQoYhMCycKCSEDENgWDifFzlcPvJiISwAAAAASUVORK5CYII=";
    }

    public Driver getDriver(String driverId) {
        String serviceUrl = discoveryClient.getInstances("driver-service")
                .stream()
                .findFirst()
                .map(si -> si.getUri().toString())
                .orElseThrow(() -> new IllegalStateException("Service not found"));

        return webClientBuilder.build()
                .get()
                .uri(serviceUrl + "/drivers/{id}", driverId)
                .retrieve()
                .bodyToMono(Driver.class)
                .block();
    }
}
