package com.fiap.techchallenge.monitoring_consumer.controller;

import com.fiap.techchallenge.monitoring_consumer.exceptions.ResourceNotFoundException;
import com.fiap.techchallenge.monitoring_consumer.model.ParkingSessionModel;
import com.fiap.techchallenge.monitoring_consumer.repository.ParkingSessionRepository;
import com.fiap.techchallenge.monitoring_consumer.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Controller de recibos", description = "Controller relacionado a operações de gerar recibos")
@Log4j2
@RestController
public class ReceiptController {

    private final ReceiptService receiptService;
    private final ParkingSessionRepository parkingSessionRepository;

    public ReceiptController(ReceiptService receiptService, ParkingSessionRepository parkingSessionRepository) {
        this.receiptService = receiptService;
        this.parkingSessionRepository = parkingSessionRepository;
    }

    @Operation(summary = "Recibo estacionamento", description = "Gera um recibo de estacionamento")
    @GetMapping("/parking-receipt/{sessionId}")
    String getParkingReceipt(@PathVariable String sessionId) {
        ParkingSessionModel sessionModel = parkingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada!"));
       return receiptService.generateParkingReceipt(sessionModel);
    }

    @Operation(summary = "Recibo de pagamento", description = "Gera um recibo de pagamento")
    @GetMapping("/payment-receipt/{paymentId}")
    String getPaymentReceipt(@PathVariable String paymentId) {
        return receiptService.generatePaymentReceipt(paymentId);
    }
}
