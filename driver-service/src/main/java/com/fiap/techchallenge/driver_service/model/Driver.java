package com.fiap.techchallenge.driver_service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "drivers")
public class Driver {
    @Id
    private String id;
    @NotBlank(message = "Nome é obrigatório")
    private String name;
    private String email;
    @NotBlank(message = "Celular é obrigatório")
    private String phone;
    @NotNull(message = "A forma de pagamento é obrigatória")
    private PaymentMethod paymentMethod;

    @DBRef
    private List<Vehicle> vehicles;
}
