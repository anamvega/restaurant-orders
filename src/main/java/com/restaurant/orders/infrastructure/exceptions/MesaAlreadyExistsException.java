package com.restaurant.orders.infrastructure.exceptions;

/**
 * Exception thrown when creating a mesa (table) whose number is already in use.
 */
public class MesaAlreadyExistsException extends RuntimeException {

    private final Integer number;

    public MesaAlreadyExistsException(Integer number) {
        super("Mesa number " + number + " already exists");
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }
}
