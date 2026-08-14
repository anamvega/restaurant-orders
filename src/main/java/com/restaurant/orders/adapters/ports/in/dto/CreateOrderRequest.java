package com.restaurant.orders.adapters.ports.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new order.
 */
@Data
public class CreateOrderRequest {

    @NotNull(message = "Table number is required")
    @Min(value = 1, message = "Table number must be at least 1")
    private Integer tableNumber;

    @NotNull(message = "Items are required")
    private OrderItemRequest[] items;
}

/**
 * Request DTO for an item in a create order request.
 */
@Data
public class OrderItemRequest {

    @NotBlank(message = "Dish name is required")
    private String dishName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price must be positive")
    private BigDecimal unitPrice;
}
