package com.restaurant.orders.infrastructure.exceptions;

/**
 * Exception thrown when an order operation violates business rules.
 */
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }

    public InvalidOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
