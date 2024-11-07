package com.bank.mortgage.infrastructure.adapter.in.rest.exception;


import com.bank.mortgage.domain.exception.MortgageDomainException;
import com.bank.mortgage.domain.exception.MortgageNotFoundException;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

/**
 * This class handle and map exceptions into the desired http error codes
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        // concat all field errors, in case there's more than one
        String errors = ex.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.joining(", "))))
                .entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), errors);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        // concat all field errors, in case there's more than one
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))))
                .entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), errors);
    }

    @ResponseBody
    @ExceptionHandler(value = {MortgageNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(MortgageNotFoundException mortgageNotFoundException) {
        log.error(mortgageNotFoundException.getMessage(), mortgageNotFoundException);
        return buildErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), mortgageNotFoundException.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {MortgageDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(MortgageDomainException mortgageDomainException) {
        log.error(mortgageDomainException.getMessage(), mortgageDomainException);
        return buildErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), mortgageDomainException.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(IllegalArgumentException illegalArgumentException) {
        log.error(illegalArgumentException.getMessage(), illegalArgumentException);
        return buildErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), illegalArgumentException.getMessage());
    }

    private ErrorResponse buildErrorResponse(String errorCode, String errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorMessage(errorMessage);
        return errorResponse;
    }


}