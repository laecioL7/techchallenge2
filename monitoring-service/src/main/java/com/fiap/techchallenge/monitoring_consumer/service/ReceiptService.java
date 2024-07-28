package com.fiap.techchallenge.monitoring_consumer.service;

import com.fiap.techchallenge.monitoring_consumer.client.DriverClient;
import com.fiap.techchallenge.monitoring_consumer.client.PaymentClient;
import com.fiap.techchallenge.monitoring_consumer.model.Driver;
import com.fiap.techchallenge.monitoring_consumer.model.ParkingSessionModel;
import com.fiap.techchallenge.monitoring_consumer.model.PaymentResume;
import com.fiap.techchallenge.monitoring_consumer.model.Vehicle;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
@Log4j2
public class ReceiptService {


    @Value("${receipt.output-dir}")
    String outputDir;

    private final DriverClient driverClient;
    private final PaymentClient paymentClient;

    public ReceiptService(DriverClient driverClient, PaymentClient paymentClient) {
        this.driverClient = driverClient;
        this.paymentClient = paymentClient;
    }

    public String generateParkingReceipt(ParkingSessionModel parkingSessionModel) {
        //Esse ponto seria feito em outro ms de relatório mas para diminuir o escopo da entrega fiz aqui mesmo
        return createParkingReceiptPdf(parkingSessionModel);
    }

    public String generatePaymentReceipt(String paymentId) {
        log.info("Obtendo dados de pagamento..." + paymentId);
        PaymentResume paymentResume = paymentClient.getPaymentResume(paymentId);
        return createPaymentReceiptPdf(paymentResume);
    }

    private String createParkingReceiptPdf(ParkingSessionModel parkingSessionModel) {
        //dados usuario
        Driver driver = driverClient.getDriver(parkingSessionModel.getDriverId());

        String driverName = driver.getName();
        String email = driver.getEmail();
        String phone = driver.getPhone();;
        //veiculo
        Vehicle vehicle = driver.getVehicles().get(0);
        String vehiclePlate = vehicle.getLicensePlate();
        String vehicleModel = vehicle.getModel();

        //sessão de estacionamento
        String startTime = parkingSessionModel.getStartTime().toString();
        String endTime = parkingSessionModel.getEndTime().toString();
        String userFinishedTime = parkingSessionModel.getUserFinishTime() != null ?
                parkingSessionModel.getUserFinishTime().toString() : "";
        String totalAmount = "Valor total disponível no recibo de pagamento";

        // Criar documento PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configurar fonte
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 750);
                contentStream.showText(driverName);
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 730);
                contentStream.showText(email);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 715);
                contentStream.showText("Telefone: " + phone);
                contentStream.endText();

                // Adicionar linha separadora
                contentStream.setLineWidth(1);
                contentStream.moveTo(100, 700);
                contentStream.lineTo(500, 700);
                contentStream.stroke();

                // Adicionar detalhes do recibo
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 14);
                contentStream.newLineAtOffset(100, 680);
                contentStream.showText("Data de termino pelo usuário: " + userFinishedTime);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 660);
                contentStream.showText("Entrada: " + startTime);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 640);
                contentStream.showText("Fim: " + endTime);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 620);
                contentStream.showText("Placa do Veículo: " + vehiclePlate);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 600);
                contentStream.showText("Total: " + totalAmount);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 580);
                contentStream.showText("Modelo do veículo: " + vehicleModel);
                contentStream.endText();
            }

            // Salvar documento
            document.save(outputDir + "parking_receipt.pdf");
            log.info("Salvando arquivo na pasta: {}", outputDir);
            log.info("Recibo de estacionamento criado com sucesso!");
            // Salvar o documento em um ByteArrayOutputStream
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                document.save(out);
                return java.util.Base64.getEncoder().encodeToString(out.toByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createPaymentReceiptPdf(PaymentResume paymentResume) {
        //dados usuario
        Driver driver = paymentResume.getDriver();

        String driverName = driver.getName();
        String email = driver.getEmail();
        String phone = driver.getPhone();;

        //sessão de estacionamento
        ParkingSessionModel parkingSessionModel = paymentResume.getParkingSessionModel();

        String startTime = parkingSessionModel.getStartTime().toString();
        String endTime = parkingSessionModel.getEndTime().toString();
        String userFinishedTime = parkingSessionModel.getUserFinishTime() != null ?
                parkingSessionModel.getUserFinishTime().toString() : "";
        String totalAmount = "R$" + paymentResume.getTotalPayment();

        // Criar documento PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configurar fonte
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 750);
                contentStream.showText(driverName);
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 730);
                contentStream.showText(email);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 715);
                contentStream.showText("Telefone: " + phone);
                contentStream.endText();

                // Adicionar linha separadora
                contentStream.setLineWidth(1);
                contentStream.moveTo(100, 700);
                contentStream.lineTo(500, 700);
                contentStream.stroke();

                // Adicionar detalhes do recibo
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 14);
                contentStream.newLineAtOffset(100, 680);
                contentStream.showText("Data de termino pelo usuário: " + userFinishedTime);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 660);
                contentStream.showText("Entrada: " + startTime);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 640);
                contentStream.showText("Fim: " + endTime);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 600);
                contentStream.showText("Total: " + totalAmount);
                contentStream.endText();

                // Adicionar linha separadora
                contentStream.setLineWidth(1);
                contentStream.moveTo(100, 560);
                contentStream.lineTo(500, 560);
                contentStream.stroke();

                //dados pagamento
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 540);
                contentStream.showText("Total de horas estacionado: " + paymentResume.getTotalHours());
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 520);
                contentStream.showText("Status do pagamento: " + paymentResume.getStatus());
                contentStream.endText();
            }

            // Salvar documento
            document.save(outputDir + "payment_receipt.pdf");
            log.info("Salvando arquivo na pasta: {}", outputDir);
            log.info("Recibo de estacionamento criado com sucesso!");
            // Salvar o documento em um ByteArrayOutputStream
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                document.save(out);
                return java.util.Base64.getEncoder().encodeToString(out.toByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}