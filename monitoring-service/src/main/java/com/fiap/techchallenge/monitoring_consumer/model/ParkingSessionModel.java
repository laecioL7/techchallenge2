package com.fiap.techchallenge.monitoring_consumer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class ParkingSessionModel {
    @Id
    private String id;
    private String driverId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime userFinishTime;
    private boolean notified;
    private boolean finished;

    public ParkingSessionModel(String driverId, LocalDateTime endTime, boolean notified) {
        this.driverId = driverId;
        this.startTime = LocalDateTime.now();
        this.endTime = endTime;
        this.notified = notified;
        this.finished = false;
    }
}
