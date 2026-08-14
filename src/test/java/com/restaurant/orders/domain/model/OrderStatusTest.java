package com.restaurant.orders.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void shouldAllowTransitionFromPendingToPreparing() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.PREPARING));
    }

    @Test
    void shouldAllowTransitionFromPendingToCancelled() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED));
    }

    @Test
    void shouldNotAllowTransitionFromPendingToServed() {
        assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.SERVED));
    }

    @Test
    void shouldAllowTransitionFromPreparingToServed() {
        assertTrue(OrderStatus.PREPARING.canTransitionTo(OrderStatus.SERVED));
    }

    @Test
    void shouldAllowTransitionFromPreparingToCancelled() {
        assertTrue(OrderStatus.PREPARING.canTransitionTo(OrderStatus.CANCELLED));
    }

    @Test
    void shouldAllowTransitionFromServedToDelivered() {
        assertTrue(OrderStatus.SERVED.canTransitionTo(OrderStatus.DELIVERED));
    }

    @Test
    void shouldNotAllowTransitionFromDeliveredToAny() {
        assertFalse(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.PENDING));
        assertFalse(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CANCELLED));
    }

    @Test
    void shouldNotAllowTransitionFromCancelledToAny() {
        assertFalse(OrderStatus.CANCELLED.canTransitionTo(OrderStatus.PREPARING));
        assertFalse(OrderStatus.CANCELLED.canTransitionTo(OrderStatus.SERVED));
    }

    @Test
    void shouldIdentifyTerminalStates() {
        assertTrue(OrderStatus.DELIVERED.isTerminal());
        assertTrue(OrderStatus.CANCELLED.isTerminal());
        assertFalse(OrderStatus.PENDING.isTerminal());
        assertFalse(OrderStatus.PREPARING.isTerminal());
        assertFalse(OrderStatus.SERVED.isTerminal());
    }

    @Test
    void shouldIdentifyModifiableStates() {
        assertTrue(OrderStatus.PENDING.isModifiable());
        assertTrue(OrderStatus.PREPARING.isModifiable());
        assertFalse(OrderStatus.SERVED.isModifiable());
        assertFalse(OrderStatus.DELIVERED.isModifiable());
        assertFalse(OrderStatus.CANCELLED.isModifiable());
    }
}
