package com.fiap.techchallenge.driver_service.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "vehicles")
public class Vehicle {
    @Id
    private String id;
    @NotBlank(message = "Modelo é obrigatório")
    private String model;
    @NotBlank(message = "Placa é obrigatória")
    private String licensePlate;
}
