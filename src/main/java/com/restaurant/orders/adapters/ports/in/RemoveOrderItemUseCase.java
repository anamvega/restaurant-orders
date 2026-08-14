package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Order;
import reactor.core.publisher.Mono;

/**
 * Use Case: Remove an item from an existing order.
 */
public interface RemoveOrderItemUseCase {
    Mono<Order> execute(String orderId, String dishName);
}
