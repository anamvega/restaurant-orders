package com.restaurant.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.restaurant.orders.domain.util.ErrorMessages.*;

/**
 * Value Object representing a single item in an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String dishName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    /**
     * Factory method to create an OrderItem with calculated subtotal.
     */
    public static OrderItem of(String dishName, Integer quantity, BigDecimal unitPrice) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(format(MUST_BE_POSITIVE, FIELD_QUANTITY));
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(format(MUST_BE_POSITIVE, FIELD_UNIT_PRICE));
        }
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new OrderItem(dishName, quantity, unitPrice, subtotal);
    }

    /**
     * Recalculate subtotal based on new quantity.
     */
    public OrderItem withQuantity(Integer newQuantity) {
        return OrderItem.of(this.dishName, newQuantity, this.unitPrice);
    }
}
