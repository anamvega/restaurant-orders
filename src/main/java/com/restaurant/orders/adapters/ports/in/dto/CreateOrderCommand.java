package com.restaurant.orders.adapters.ports.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Command object for creating an order.
 */
public record CreateOrderCommand(
        @NotNull(message = "Table number is required")
        @Min(value = 1, message = "Table number must be at least 1")
        Integer tableNumber,

        @NotNull(message = "Items are required")
        CreateOrderItemCommand[] items
) {
    public boolean hasItems() {
        return items != null && items.length > 0;
    }
}
