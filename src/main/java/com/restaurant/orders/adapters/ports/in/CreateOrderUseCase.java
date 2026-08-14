package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.adapters.ports.in.dto.CreateOrderCommand;
import com.restaurant.orders.domain.model.Order;
import reactor.core.publisher.Mono;

/**
 * Use Case: Create a new restaurant table order.
 */
public interface CreateOrderUseCase {
    Mono<Order> execute(CreateOrderCommand command);
}
