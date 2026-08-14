package com.restaurant.orders.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithCalculatedTotal() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 2, new BigDecimal("45.00")),
                OrderItem.of("Agua de Horchata", 2, new BigDecimal("25.00"))
        );

        Order order = Order.of(5, items);

        assertEquals(5, order.getTableNumber());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(new BigDecimal("140.00"), order.getTotalAmount());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void shouldRejectNullTableNumber() {
        List<OrderItem> items = List.of(OrderItem.of("Tacos", 1, new BigDecimal("45.00")));
        assertThrows(IllegalArgumentException.class, () -> Order.of(null, items));
    }

    @Test
    void shouldRejectZeroTableNumber() {
        List<OrderItem> items = List.of(OrderItem.of("Tacos", 1, new BigDecimal("45.00")));
        assertThrows(IllegalArgumentException.class, () -> Order.of(0, items));
    }

    @Test
    void shouldRejectNullItems() {
        assertThrows(IllegalArgumentException.class, () -> Order.of(1, null));
    }

    @Test
    void shouldRejectEmptyItems() {
        assertThrows(IllegalArgumentException.class, () -> Order.of(1, List.of()));
    }

    @Test
    void shouldAddItemToOrder() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 2, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);

        OrderItem newItem = OrderItem.of("Cerveza", 4, new BigDecimal("35.00"));
        order.addItem(newItem);

        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("230.00"), order.getTotalAmount());
    }

    @Test
    void shouldMergeItemWithExistingDish() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 2, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);

        OrderItem duplicateItem = OrderItem.of("Tacos al Pastor", 3, new BigDecimal("45.00"));
        order.addItem(duplicateItem);

        assertEquals(1, order.getItems().size());
        assertEquals(5, order.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("225.00"), order.getItems().get(0).getSubtotal());
    }

    @Test
    void shouldRemoveItemFromOrder() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 2, new BigDecimal("45.00")),
                OrderItem.of("Agua de Horchata", 2, new BigDecimal("25.00"))
        );
        Order order = Order.of(1, items);

        boolean removed = order.removeItem("Agua de Horchata");

        assertTrue(removed);
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("90.00"), order.getTotalAmount());
    }

    @Test
    void shouldReturnFalseWhenRemovingNonExistentItem() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 2, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);

        boolean removed = order.removeItem("Pizza");

        assertFalse(removed);
    }

    @Test
    void shouldUpdateStatusFromPendingToPreparing() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 1, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);

        order.updateStatus(OrderStatus.PREPARING);

        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }

    @Test
    void shouldUpdateStatusFromPreparingToServed() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 1, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);
        order.updateStatus(OrderStatus.PREPARING);

        order.updateStatus(OrderStatus.SERVED);

        assertEquals(OrderStatus.SERVED, order.getStatus());
    }

    @Test
    void shouldUpdateStatusFromServedToDelivered() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 1, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);
        order.updateStatus(OrderStatus.PREPARING);
        order.updateStatus(OrderStatus.SERVED);

        order.updateStatus(OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void shouldNotAllowInvalidStatusTransition() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 1, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);

        assertThrows(IllegalArgumentException.class, () ->
                order.updateStatus(OrderStatus.DELIVERED));
    }

    @Test
    void shouldNotAllowModifyingTerminalOrder() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 1, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);
        order.updateStatus(OrderStatus.CANCELLED);

        assertThrows(IllegalStateException.class, () ->
                order.addItem(OrderItem.of("Cerveza", 1, new BigDecimal("35.00"))));
    }

    @Test
    void shouldNotAllowRemovingItemFromCancelledOrder() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 1, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);
        order.updateStatus(OrderStatus.CANCELLED);

        assertThrows(IllegalStateException.class, () ->
                order.removeItem("Tacos al Pastor"));
    }

    @Test
    void shouldReturnImmutableItemsList() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 1, new BigDecimal("45.00"))
        );
        Order order = Order.of(1, items);

        assertThrows(UnsupportedOperationException.class, () ->
                order.getItems().add(OrderItem.of("Extra", 1, new BigDecimal("10.00"))));
    }
}
