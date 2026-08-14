package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Order;
import reactor.core.publisher.Flux;

/**
 * Use Case: List all orders with optional filters.
 */
public interface ListOrdersUseCase {
    Flux<Order> execute(ListOrdersFilter filter);
}
