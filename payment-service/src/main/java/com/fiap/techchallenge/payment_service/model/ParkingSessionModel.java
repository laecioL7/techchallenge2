package com.fiap.techchallenge.payment_service.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParkingSessionModel {
    private String driverId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime userFinishTime;
}
