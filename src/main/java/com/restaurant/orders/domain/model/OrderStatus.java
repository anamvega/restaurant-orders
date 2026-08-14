package com.restaurant.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing the possible statuses of an order.
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING("Pendiente"),
    PREPARING("Preparando"),
    SERVED("Servido"),
    DELIVERED("Entregado"),
    CANCELLED("Cancelado");

    private final String description;

    /**
     * Returns true if this status allows further modifications.
     */
    public boolean isModifiable() {
        return this == PENDING || this == PREPARING;
    }

    /**
     * Returns true if the order is in a terminal state.
     */
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Validates if a transition from this status to the target status is allowed.
     */
    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case PENDING -> target == PREPARING || target == CANCELLED;
            case PREPARING -> target == SERVED || target == CANCELLED;
            case SERVED -> target == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
