package com.fiap.techchallenge.monitoring_consumer.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fiap.techchallenge.monitoring_consumer.model.ParkingSessionModel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Document
public class ParkingSessionReponse {
    private String driverId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime userFinishTime;
    private boolean notified;
    private boolean finished;
    private String timeLeft;

    public ParkingSessionReponse(ParkingSessionModel sessionModel) {
        this.driverId = sessionModel.getDriverId();
        this.startTime = sessionModel.getStartTime();
        this.endTime = sessionModel.getEndTime();
        this.notified = sessionModel.isNotified();
        this.finished = sessionModel.isFinished();
        this.userFinishTime = sessionModel.getUserFinishTime();
    }
}
