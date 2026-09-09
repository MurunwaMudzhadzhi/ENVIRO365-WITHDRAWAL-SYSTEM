package com.enviro.assessment.junior.murunwamudzhadzhi.exception;

/**
 * Thrown when a requested investor/product/withdrawal id does not exist.
 * Mapped to HTTP 404 by the GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
