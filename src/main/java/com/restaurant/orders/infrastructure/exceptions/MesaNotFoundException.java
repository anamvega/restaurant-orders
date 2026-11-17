package com.restaurant.orders.infrastructure.exceptions;

import static com.restaurant.orders.domain.util.ErrorMessages.MESA_NOT_FOUND;
import static com.restaurant.orders.domain.util.ErrorMessages.format;

/**
 * Exception thrown when a mesa (table) is not found by ID.
 */
public class MesaNotFoundException extends RuntimeException {

    private final String mesaId;

    public MesaNotFoundException(String mesaId) {
        super(format(MESA_NOT_FOUND, mesaId));
        this.mesaId = mesaId;
    }

    public String getMesaId() {
        return mesaId;
    }
}
