package com.fiap.techchallenge.payment_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class PaymentResume {
    @Id
    private String id;
    private Long totalHours;
    //TODO salvar enum
    private PaymentMethod paymentMethod;
    private Double totalPayment;
    private PaymentStatus status;
    private Driver driver;
    private ParkingSessionModel parkingSessionModel;
}
