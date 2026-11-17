package com.restaurant.orders.infrastructure.exceptions;

import static com.restaurant.orders.domain.util.ErrorMessages.MESA_ALREADY_EXISTS;
import static com.restaurant.orders.domain.util.ErrorMessages.format;

/**
 * Exception thrown when creating a mesa (table) whose number is already in use.
 */
public class MesaAlreadyExistsException extends RuntimeException {

    private final Integer number;

    public MesaAlreadyExistsException(Integer number) {
        super(format(MESA_ALREADY_EXISTS, number));
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }
}
