package com.fiap.techchallenge.payment_service.request;

import com.fiap.techchallenge.payment_service.model.PaymentStatus;
import lombok.Data;

@Data
public class PaymentCallbackRequest {
    private String paymentId;
    private PaymentStatus paymentStatus;
    private String message;
}
