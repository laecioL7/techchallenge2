package com.fiap.techchallenge.driver_service.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "parkings")
public class Parking {
    @Id
    private String id;

    @DBRef
    private Vehicle vehicle;

    private DriverReference driver;

    /*se for fixo duration vai ter a quantidade de horas especificadas
    * se não for começa em zero e vai renovar a cada hora
    * */
    private long duration;
    private boolean fixedTime;
}
