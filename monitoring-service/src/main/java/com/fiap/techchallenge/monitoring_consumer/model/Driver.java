package com.fiap.techchallenge.monitoring_consumer.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
public class Driver {
    private String id;
    private String name;
    private String email;
    private String phone;
    private PaymentMethod paymentMethod;
    private List<Vehicle> vehicles;
}
