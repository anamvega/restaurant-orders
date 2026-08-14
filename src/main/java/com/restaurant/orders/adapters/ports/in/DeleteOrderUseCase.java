package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Order;
import reactor.core.publisher.Mono;

/**
 * Use Case: Delete (cancel) an order.
 */
public interface DeleteOrderUseCase {
    Mono<Order> execute(String orderId);
}
