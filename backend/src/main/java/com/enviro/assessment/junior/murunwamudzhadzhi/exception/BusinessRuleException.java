package com.enviro.assessment.junior.murunwamudzhadzhi.exception;

/**
 * Thrown whenever a request is well-formed but violates one of the
 * withdrawal business rules (age restriction, balance cap, 90% cap, etc.).
 * Mapped to HTTP 400 by the GlobalExceptionHandler.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
