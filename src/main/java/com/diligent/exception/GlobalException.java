package com.diligent.exception;

/**
 * Generic application-level exception for validation/business errors
 * that don't fit a more specific exception type (e.g. bad request data,
 * invalid filter params, invalid amount, etc.)
 */
public class GlobalException extends RuntimeException {

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }
}
