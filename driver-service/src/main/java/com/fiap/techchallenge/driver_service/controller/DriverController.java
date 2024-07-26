package com.fiap.techchallenge.driver_service.controller;

import com.fiap.techchallenge.driver_service.model.Driver;
import com.fiap.techchallenge.driver_service.model.Vehicle;
import com.fiap.techchallenge.driver_service.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Driver Controller", description = "Controller relacionado a operações do condutor")
@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;


    @Operation(summary = "Obter Condutores", description = "Este endpoint retorna uma lista de todos os condutores")
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @Operation(summary = "Obter Condutor", description = "Este endpoint retorna um condutor com todos os detalhes")
    @GetMapping("/{id}")
    @Cacheable(value = "driver-service", key = "#id")
    public ResponseEntity<Driver> getDriverById(@PathVariable String id) {
        Optional<Driver> driver = driverService.getDriverById(id);
        return driver.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Adicionar Condutor", description = "Salva um condutor")
    @PostMapping
    public Driver addDriver(@RequestBody @Valid Driver driver) {
        return driverService.addDriver(driver);
    }

    @Operation(summary = "Atualizar Condutor", description = "Atualiza dados do condutor e adiciona um veículo caso ele não tenha")
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable String id, @RequestBody @Valid Driver driver) {
        //verifica se o condutor existe e lança exceção se não existir
        getDriver(id);

        return ResponseEntity.ok(driverService.updateDriver(id, driver));
    }

    @Operation(summary = "Deletar Condutor", description = "Este endpoint deleta um condutor")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String id) {
        //verifica se o condutor existe e lança exceção se não existir
        getDriver(id);

        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obter Veículos do Condutor", description = "Este endpoint retorna uma lista de veículos do condutor")
    @PostMapping("/{driverId}/vehicles")
    public ResponseEntity<Vehicle> addVehicleToDriver(@PathVariable String driverId, @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(driverService.addVehicleToDriver(driverId, vehicle));
    }

    @Operation(summary = "Remover Veículo", description = "Este endpoint remove um veiculo do condutor")
    @DeleteMapping("/{driverId}/vehicles/{vehicleId}")
    public ResponseEntity<Void> removeVehicleFromDriver(@PathVariable String driverId, @PathVariable String vehicleId) {
        Driver driver = getDriver(driverId);

        boolean removed = driver.getVehicles().removeIf(vehicle -> vehicle.getId().equals(vehicleId));

        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        driverService.updateDriver(driver.getId(), driver);
        return ResponseEntity.noContent().build();
    }

    private Driver getDriver(String driverId) {
        Optional<Driver> optionalDriver = driverService.getDriverById(driverId);

        if (optionalDriver.isEmpty()) {
            throw new InvalidDataAccessResourceUsageException("Driver not found");
        }else {
            return optionalDriver.get();
        }
    }
}

