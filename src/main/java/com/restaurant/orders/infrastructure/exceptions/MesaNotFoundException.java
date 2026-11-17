package com.restaurant.orders.infrastructure.exceptions;

/**
 * Exception thrown when a mesa (table) is not found by ID.
 */
public class MesaNotFoundException extends RuntimeException {

    private final String mesaId;

    public MesaNotFoundException(String mesaId) {
        super("Mesa not found with ID: " + mesaId);
        this.mesaId = mesaId;
    }

    public String getMesaId() {
        return mesaId;
    }
}
