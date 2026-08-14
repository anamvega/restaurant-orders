package com.restaurant.orders.adapters.ports.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Command object for an order item in create command.
 */
public record CreateOrderItemCommand(
        @NotBlank(message = "Dish name is required") String dishName,
        @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity,
        @NotNull(message = "Unit price is required") @Min(value = 0, message = "Unit price must be positive") BigDecimal unitPrice
) {
}
