package com.bank.mortgage.domain.exception;

public class MortgageDomainException extends DomainException {

    public MortgageDomainException(String message) {
        super(message);
    }

    public MortgageDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}