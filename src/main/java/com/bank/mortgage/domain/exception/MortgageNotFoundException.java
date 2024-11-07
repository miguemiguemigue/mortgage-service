package com.bank.mortgage.domain.exception;

public class MortgageNotFoundException extends DomainException {

    public MortgageNotFoundException(String message) {
        super(message);
    }

    public MortgageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}