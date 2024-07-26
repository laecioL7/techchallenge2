package com.fiap.techchallenge.monitoring_consumer.service;

import com.fiap.techchallenge.monitoring_consumer.client.PaymentClient;
import com.fiap.techchallenge.monitoring_consumer.exceptions.ResourceNotFoundException;
import com.fiap.techchallenge.monitoring_consumer.model.ParkingSessionModel;
import com.fiap.techchallenge.monitoring_consumer.repository.ParkingSessionRepository;
import com.fiap.techchallenge.monitoring_consumer.request.ParkingRequest;
import com.fiap.techchallenge.monitoring_consumer.request.RegisterPaymentRequest;
import com.fiap.techchallenge.monitoring_consumer.response.ParkingSessionReponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class SchedulerService {

    @Value("${notificationTimeInMinutes}")
    private int notificationTimeInMinutes;

    @Autowired
    ParkingSessionRepository parkingSessionRepository;

    @Autowired
    PaymentClient paymentClient;

    private final ScheduledExecutorService scheduledExecutorService;
    private final Map<String, Long> scheduledStartTime = new ConcurrentHashMap<>();
    private final Map<String, Long> scheduledDuration = new ConcurrentHashMap<>();
    private final Map<String, ParkingSession> scheduledTasks = new ConcurrentHashMap<>();


    @Autowired
    public SchedulerService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public String startParking(@RequestBody ParkingRequest request) {
        String driverId = request.getDriverId();
        long durationInMinutes = request.getDuration();

        if (!request.isFixedTime()) {
            //significa que o tempo não é fixo então começa de 1h
            durationInMinutes = 60;
        }

        ParkingSessionModel session =
                new ParkingSessionModel(driverId, LocalDateTime.now().plusMinutes(durationInMinutes), false);

        String sessionId = parkingSessionRepository.save(session).getId();

        log.info("Tempo requisitado: {} | Tempo fixo? {}", durationInMinutes, request.isFixedTime() ? "Sim" : "Não");

        // Tarefa principal que será executada quando o tempo expirar
        Runnable mainTask = () -> {
            log.info("Estacionamento do condutor {} expirou.", driverId);
            // Enviar mensagem para outra fila se necessário
            session.setFinished(true);
            parkingSessionRepository.save(session);
            
            //registra os dados para pagamento
            paymentClient.registerPayment(new RegisterPaymentRequest(session));
        };

        // Tarefa de notificação que será executada minutos antes do tempo expirar
        Runnable notificationTask = () -> {
            log.info("Notificação: O tempo de estacionamento do condutor {} expirará em {} minutos.", driverId, notificationTimeInMinutes);
            // Enviar notificação para o condutor
            //TODO: não implementado nessa versão
            // notificationService.notifyDriver(session.getDriverId(), session.getId());
            session.setNotified(true);
            parkingSessionRepository.save(session);
        };

        // Agenda a tarefa principal
        ScheduledFuture<?> mainScheduledFuture = scheduledExecutorService.schedule(mainTask, durationInMinutes, TimeUnit.MINUTES);

        // Agenda a tarefa de notificação para 10 minutos antes do tempo expirar
        long notificationDelay = durationInMinutes - notificationTimeInMinutes;

        ScheduledFuture<?> notificationScheduledFuture = null;
        notificationScheduledFuture = scheduledExecutorService.schedule(notificationTask, notificationDelay, TimeUnit.MINUTES);

        scheduledTasks.put(driverId, new ParkingSession(mainScheduledFuture, notificationScheduledFuture));

        return sessionId;
    }


    public void stopParking(@RequestParam String driverId) {
        //obtem a thread
        ParkingSession session = scheduledTasks.get(driverId);

        ParkingSessionModel sessionModel = parkingSessionRepository.findByDriverIdAndFinished(driverId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de estacionamento não encontrada para o ID: " + driverId));

        if (session != null) {
            finishSessions(session);
            scheduledTasks.remove(driverId);
        }

        sessionModel.setFinished(true);
        sessionModel.setUserFinishTime(LocalDateTime.now());
        parkingSessionRepository.save(sessionModel);
        log.info("Estacionamento do condutor {} foi finalizado.", driverId);

        //registra os dados para pagamento
        paymentClient.registerPayment(new RegisterPaymentRequest(sessionModel));
    }

    private static void finishSessions(ParkingSession session) {
        //cancela todas as tasks
        if (session.getMainTask() != null) {
            session.getMainTask().cancel(false);
        }
        if (session.getNotificationTask() != null) {
            session.getNotificationTask().cancel(false);
        }
    }

    public ParkingSessionReponse getSessionStatus(String sessionId) {
        ParkingSessionModel sessionModel = parkingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de estacionamento não encontrada para o ID: " + sessionId));

        // Calcula a diferença em minutos
        Duration duration = Duration.between(LocalDateTime.now(), sessionModel.getEndTime());
        // Converte a duração em minutos
        ParkingSessionReponse reponse = new ParkingSessionReponse(sessionModel);
        reponse.setTimeLeft(duration.toMinutes() < 1 ? 0 + "" : duration.toMinutes() + " minutos");
        return reponse;
    }

    @Getter
    @Setter
    private static class ParkingSession {
        private final ScheduledFuture<?> mainTask;
        private final ScheduledFuture<?> notificationTask;

        public ParkingSession(ScheduledFuture<?> mainTask, ScheduledFuture<?> notificationTask) {
            this.mainTask = mainTask;
            this.notificationTask = notificationTask;
        }

        public ScheduledFuture<?> getMainTask() {
            return mainTask;
        }

        public ScheduledFuture<?> getNotificationTask() {
            return notificationTask;
        }
    }

    public boolean checkIfTheDriverAlreadyHasAnActiveParkingPeriod(String driverId) {
        // Verifica se já existe uma tarefa agendada para este driverId
        Optional<ParkingSessionModel> sessionModel = parkingSessionRepository.findByDriverIdAndFinished(driverId, false);

        if (sessionModel.isPresent() && !sessionModel.get().isFinished()) {
            log.warn("Já existe uma sessão de estacionamento ativa para o condutor {}", driverId);
            return true; // Opcionalmente, você pode lançar uma exceção em vez de apenas retornar
        } else return false;
    }

}
