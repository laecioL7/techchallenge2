package com.fiap.techchallenge.payment_service.exceptions;

public class InvalidAccessException extends RuntimeException {
    public InvalidAccessException(String message) {
        super(message);
    }
}
