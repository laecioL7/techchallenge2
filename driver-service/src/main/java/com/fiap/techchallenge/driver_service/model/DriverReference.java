package com.fiap.techchallenge.driver_service.model;

import lombok.Data;

@Data
public class DriverReference {
    private String id;
    private String name;
    private PaymentMethod paymentMethod;
}
