package com.fiap.techchallenge.payment_service.repository;

import com.fiap.techchallenge.payment_service.model.PaymentResume;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<PaymentResume, String> {
}
