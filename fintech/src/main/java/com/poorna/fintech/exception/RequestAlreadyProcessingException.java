package com.poorna.fintech.exception;

/**
 * RequestAlreadyProcessingException
 */
public class RequestAlreadyProcessingException extends RuntimeException {
    public RequestAlreadyProcessingException(String message) {
        super(message);
    }

}
