package com.fiap.techchallenge.monitoring_consumer.repository;

import com.fiap.techchallenge.monitoring_consumer.model.ParkingSessionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ParkingSessionRepository extends MongoRepository<ParkingSessionModel, String> {

    Optional<ParkingSessionModel> findByDriverIdAndFinished(String driverId, boolean finished);
}
