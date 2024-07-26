package com.fiap.techchallenge.payment_service.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fiap.techchallenge.payment_service.model.ParkingSessionModel;
import lombok.Data;

@Data
public class RegisterPaymentRequest {
    @JsonAlias("sessionModel")
    private ParkingSessionModel sessionModel;
}
