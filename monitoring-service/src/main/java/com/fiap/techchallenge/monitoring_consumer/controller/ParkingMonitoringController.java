package com.fiap.techchallenge.monitoring_consumer.controller;


import com.fiap.techchallenge.monitoring_consumer.exceptions.InvalidAccessException;
import com.fiap.techchallenge.monitoring_consumer.request.ParkingRequest;
import com.fiap.techchallenge.monitoring_consumer.response.ParkingSessionReponse;
import com.fiap.techchallenge.monitoring_consumer.service.SchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Controller de estacionamento", description = "Controller relacionado a operações de estacionamento e monitoramento")
@Log4j2
@RestController
public class ParkingMonitoringController {

    @Value("${notificationTimeInMinutes}")
    private int notificationTimeInMinutes;

    @Autowired
    SchedulerService schedulerService;

    @Operation(summary = "Iniciar estacionamento", description = "Iniciar um periodo de estacionamento")
    @PostMapping("/start")
    public ResponseEntity<String> registerParking(@RequestBody ParkingRequest request) {
        log.info("Iniciando periodo de estacionamento para o motorista {}", request.getDriverId());

        if(schedulerService.checkIfTheDriverAlreadyHasAnActiveParkingPeriod(request.getDriverId())){
            throw new InvalidAccessException("Já existe uma sessão de estacionamento ativa para o condutor " + request.getDriverId());
        }

       return ResponseEntity.ok(schedulerService.startParking(request));
    }

    @Operation(summary = "Finalizar estacionamento", description = "Finaliza um periodo de estacionamento")
    @PostMapping("/stop/{driverId}")
    public void stopParking(@PathVariable String driverId) {
        log.info("Finalizando periodo de estacionamento para o motorista {}", driverId);
       schedulerService.stopParking(driverId);
    }

    @Operation(summary = "Status do estacionamento", description = "Obtem detalhes e status de um periodo de estacionamento")
    @GetMapping("/status/{sessionId}")
    public ResponseEntity<ParkingSessionReponse> getTimeLeft(@PathVariable String sessionId) {
        ParkingSessionReponse parkingSessionReponse = schedulerService.getSessionStatus(sessionId);
        log.info("Tempo restante de estacionamento para o motorista {} : {} minutos",
                parkingSessionReponse.getDriverId(), parkingSessionReponse.getTimeLeft());
        return ResponseEntity.ok(parkingSessionReponse);
    }
}
