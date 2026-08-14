package com.restaurant.orders.adapters.drivers.in.http;

/**
 * Request DTO for updating mesa status.
 */
public record MesaStatusRequest(
        String status
) {
}
