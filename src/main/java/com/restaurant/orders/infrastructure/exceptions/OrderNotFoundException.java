package com.restaurant.orders.infrastructure.exceptions;

/**
 * Exception thrown when an order is not found by ID.
 */
public class OrderNotFoundException extends RuntimeException {

    private final String orderId;

    public OrderNotFoundException(String orderId) {
        super("Order not found with ID: " + orderId);
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
