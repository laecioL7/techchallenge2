package com.fiap.techchallenge.monitoring_consumer.request;

import lombok.Data;

@Data
public class ParkingRequest {
    private String licensePlate;

    private String driverId;

    /*se for fixo duration vai ter a quantidade de horas especificadas
    * se não for começa em zero e vai renovar a cada hora
    * */
    private long duration;
    private boolean fixedTime;
}
