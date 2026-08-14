package com.restaurant.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root representing a restaurant table order.
 * 
 * This entity encapsulates all business rules for order management:
 * - Status transitions are validated
 * - Total amount is automatically calculated from items
 * - Timestamps are managed automatically
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private Integer tableNumber;

    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    private BigDecimal totalAmount;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Domain method: Calculate the total amount from all order items.
     */
    public void recalculateTotal() {
        this.totalAmount = this.items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Domain method: Add an item to the order following business rules.
     */
    public void addItem(OrderItem item) {
        validateNotInTerminalState();
        
        // Check if item with same dish name already exists
        boolean found = false;
        for (OrderItem existing : this.items) {
            if (existing.getDishName().equalsIgnoreCase(item.getDishName())) {
                // Update quantity of existing item
                int newQuantity = existing.getQuantity() + item.getQuantity();
                this.items.remove(existing);
                this.items.add(OrderItem.of(item.getDishName(), newQuantity, item.getUnitPrice()));
                found = true;
                break;
            }
        }
        
        if (!found) {
            this.items.add(item);
        }
        
        recalculateTotal();
    }

    /**
     * Domain method: Remove an item from the order by dish name.
     * @return true if item was removed, false if not found
     */
    public boolean removeItem(String dishName) {
        validateNotInTerminalState();
        
        boolean removed = this.items.removeIf(item -> 
                item.getDishName().equalsIgnoreCase(dishName));
        
        if (removed) {
            recalculateTotal();
        }
        
        return removed;
    }

    /**
     * Domain method: Update the order status following state machine rules.
     */
    public void updateStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalArgumentException(
                    String.format("Cannot transition from %s to %s", this.status, newStatus));
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Domain method: Validate that the order is not in a terminal state.
     */
    private void validateNotInTerminalState() {
        if (this.status.isTerminal()) {
            throw new IllegalStateException(
                    String.format("Cannot modify order in terminal state: %s", this.status));
        }
    }

    /**
     * Factory method: Create a new order with automatic timestamp initialization.
     */
    public static Order of(Integer tableNumber, List<OrderItem> items) {
        if (tableNumber == null || tableNumber <= 0) {
            throw new IllegalArgumentException("Table number must be greater than 0");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        Order order = Order.builder()
                .tableNumber(tableNumber)
                .items(new ArrayList<>(items))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        order.recalculateTotal();
        return order;
    }

    /**
     * Get an immutable copy of the items list.
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(this.items);
    }
}
