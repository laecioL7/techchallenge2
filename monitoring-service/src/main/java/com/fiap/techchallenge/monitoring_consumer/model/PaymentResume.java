package com.fiap.techchallenge.monitoring_consumer.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class PaymentResume {
    private String id;
    private Long totalHours;
    private PaymentMethod paymentMethod;
    private Double totalPayment;
    private PaymentStatus status;
    private Driver driver;
    private ParkingSessionModel parkingSessionModel;
}
