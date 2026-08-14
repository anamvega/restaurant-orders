package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderStatus;
import reactor.core.publisher.Mono;

/**
 * Use Case: Update the status of an order.
 */
public interface UpdateOrderStatusUseCase {
    Mono<Order> execute(String orderId, OrderStatus newStatus);
}
