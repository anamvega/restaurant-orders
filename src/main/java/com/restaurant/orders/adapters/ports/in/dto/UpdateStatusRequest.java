package com.restaurant.orders.adapters.ports.in.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for updating an order status.
 */
@Data
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private String status;
}
