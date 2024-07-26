package com.fiap.techchallenge.driver_service.service;
import com.fiap.techchallenge.driver_service.model.Driver;
import com.fiap.techchallenge.driver_service.model.Vehicle;
import com.fiap.techchallenge.driver_service.repositorys.DriverRepository;
import com.fiap.techchallenge.driver_service.repositorys.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriverById(String id) {
        return driverRepository.findById(id);
    }

    public Driver addDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public Driver updateDriver(String id, Driver driver) {
        driver.setId(id);
        return driverRepository.save(driver);
    }

    public void deleteDriver(String id) {
        driverRepository.deleteById(id);
    }

    public Vehicle addVehicleToDriver(String driverId, Vehicle vehicle) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        driver.getVehicles().add(savedVehicle);
        driverRepository.save(driver);
        return savedVehicle;
    }
}

