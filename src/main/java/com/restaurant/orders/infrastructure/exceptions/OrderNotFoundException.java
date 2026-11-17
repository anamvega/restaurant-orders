package com.restaurant.orders.infrastructure.exceptions;

import static com.restaurant.orders.domain.util.ErrorMessages.ORDER_NOT_FOUND;
import static com.restaurant.orders.domain.util.ErrorMessages.format;

/**
 * Exception thrown when an order is not found by ID.
 */
public class OrderNotFoundException extends RuntimeException {

    private final String orderId;

    public OrderNotFoundException(String orderId) {
        super(format(ORDER_NOT_FOUND, orderId));
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
