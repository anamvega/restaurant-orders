package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Order;
import reactor.core.publisher.Mono;

/**
 * Use Case: Get an order by its ID.
 */
public interface GetOrderUseCase {
    Mono<Order> execute(String orderId);
}
