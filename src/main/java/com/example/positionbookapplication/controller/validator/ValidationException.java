package com.example.positionbookapplication.controller.validator;

/**
 * Exception wrapper for validation
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
