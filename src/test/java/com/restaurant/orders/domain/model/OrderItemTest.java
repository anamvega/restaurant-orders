package com.restaurant.orders.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void shouldCreateOrderItemWithCalculatedSubtotal() {
        OrderItem item = OrderItem.of("Tacos al Pastor", 3, new BigDecimal("45.00"));

        assertEquals("Tacos al Pastor", item.getDishName());
        assertEquals(3, item.getQuantity());
        assertEquals(new BigDecimal("45.00"), item.getUnitPrice());
        assertEquals(new BigDecimal("135.00"), item.getSubtotal());
    }

    @Test
    void shouldRejectZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () ->
                OrderItem.of("Tacos", 0, new BigDecimal("45.00")));
    }

    @Test
    void shouldRejectNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () ->
                OrderItem.of("Tacos", -1, new BigDecimal("45.00")));
    }

    @Test
    void shouldRejectZeroUnitPrice() {
        assertThrows(IllegalArgumentException.class, () ->
                OrderItem.of("Tacos", 1, BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeUnitPrice() {
        assertThrows(IllegalArgumentException.class, () ->
                OrderItem.of("Tacos", 1, new BigDecimal("-10.00")));
    }

    @Test
    void shouldRecalculateWithNewQuantity() {
        OrderItem item = OrderItem.of("Tacos", 2, new BigDecimal("50.00"));
        OrderItem updated = item.withQuantity(5);

        assertEquals(5, updated.getQuantity());
        assertEquals(new BigDecimal("250.00"), updated.getSubtotal());
    }
}
