package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Order;
import reactor.core.publisher.Mono;

/**
 * Use Case: Add an item to an existing order.
 */
public interface AddOrderItemUseCase {
    Mono<Order> execute(String orderId, AddOrderItemCommand command);
}
