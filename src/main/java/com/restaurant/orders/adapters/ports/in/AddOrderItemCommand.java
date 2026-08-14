package com.restaurant.orders.adapters.ports.in;

import java.math.BigDecimal;

/**
 * Command object for adding an item to an order.
 */
public record AddOrderItemCommand(
        String dishName,
        Integer quantity,
        BigDecimal unitPrice
) {
}
