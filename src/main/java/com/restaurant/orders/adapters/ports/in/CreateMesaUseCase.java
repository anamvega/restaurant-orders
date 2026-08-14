package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Mesa;
import reactor.core.publisher.Mono;

/**
 * Use Case: Create a new restaurant table.
 */
public interface CreateMesaUseCase {
    Mono<Mesa> execute(Integer number, Integer capacity);
}
