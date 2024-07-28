package com.fiap.techchallenge.driver_service.repositorys;

import com.fiap.techchallenge.driver_service.model.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {
}
