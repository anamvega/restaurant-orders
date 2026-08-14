package com.restaurant.orders.adapters.drivers.in.http;

/**
 * Request DTO for creating a new mesa (table).
 */
public record MesaCreateRequest(
        Integer number,
        Integer capacity
) {
}
