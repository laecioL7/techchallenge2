package com.fiap.techchallenge.monitoring_consumer.request;

import lombok.Data;
import com.fiap.techchallenge.monitoring_consumer.model.ParkingSessionModel;

@Data
public class RegisterPaymentRequest {
    private ParkingSessionModel sessionModel;

    public RegisterPaymentRequest(ParkingSessionModel sessionModel) {
        this.sessionModel = sessionModel;
    }
}
