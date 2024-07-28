package com.fiap.techchallenge.payment_service.model;

import lombok.Data;

@Data
public class Driver {
    private String name;
    private String email;
    private String phone;
    private PaymentMethod paymentMethod;
}
