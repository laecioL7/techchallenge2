package com.fiap.techchallenge.payment_service.controller;

import com.fiap.techchallenge.payment_service.exceptions.ResourceNotFoundException;
import com.fiap.techchallenge.payment_service.model.PaymentMethod;
import com.fiap.techchallenge.payment_service.model.PaymentResume;
import com.fiap.techchallenge.payment_service.model.PaymentStatus;
import com.fiap.techchallenge.payment_service.request.PaymentCallbackRequest;
import com.fiap.techchallenge.payment_service.request.RegisterPaymentRequest;
import com.fiap.techchallenge.payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Controller de pagamento", description = "Controller destinado a operações de pagamento")
@Log4j2
@RestController
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Operation(summary = "Registrar Pagamento",description = "Registra um pagamento de uma sessão de estacionamento")
    @PostMapping("/register-payment")
    public ResponseEntity<String> registerParking(@RequestBody RegisterPaymentRequest request) {
        log.info("Gravando pagamento para o motorista {}", request.getSessionModel().getDriverId());
        return ResponseEntity.ok(paymentService.registerPayment(request.getSessionModel()));
    }


    @Operation(summary = "Resumo de Pagamento",description = "Obtem detalhes de um pagamento")
    @GetMapping("/payment-resume/{paymentId}")
    public ResponseEntity<PaymentResume> getResume(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentResume(paymentId));
    }



    @Operation(summary = "Pagar",description = "Realiza o pagamento por via de um gateway externo de pagamentos")
    //TODO: Não implementado uma integração real devido ao tempo e por não estar especificado no doc
    @PostMapping("/pay/{paymentId}")
    public ResponseEntity<String> pay(@PathVariable String paymentId) {
        PaymentResume paymentResume = paymentService.getPaymentResume(paymentId);

        if(paymentResume.getPaymentMethod().equals(PaymentMethod.PIX)){
            return ResponseEntity.ok(paymentService.gerarQRCode());
        }else{
            log.info("Realizando integração de pagamento com plataforma");
            log.info("...");
            log.info("...");
            return ResponseEntity.ok("Realize o pagamento em: https://www.paypal.com/");
        }

    }


    @Operation(summary = "Callback de Pagamento",description = "Recebe a resposta da operação de pagamento e atualiza")
    //**Se fosse um sistema real trafegaria um código uuid para evitar o uso do id do banco
    //TODO: callback de pagamento com plataformas como paypal ou pagseguro
    @PostMapping("/payment-result/{paymentId}")
    public ResponseEntity<String> payResult(@PathVariable PaymentCallbackRequest request) {
        PaymentResume paymentResume = paymentService.getPaymentResume(request.getPaymentId());

       if(request.getPaymentStatus().equals(PaymentStatus.CONCLUIDO)){
           return ResponseEntity.ok("Realize o pagamento em: https://www.paypal.com/");
       }else if(request.getPaymentStatus().equals(PaymentStatus.ERRO)){
           //mostra mensagem da plataforma de pagamento:
           return ResponseEntity.ok((request.getMessage() != null && !request.getMessage().isEmpty())
                   ? request.getMessage() :
                   "Houve um erro ao realizar o pagamento! Retorne a plataforma e tente novamente");
       }

       throw new ResourceNotFoundException("Pagamento ainda pendente!! Tente novamente mais tarde");
    }
}
