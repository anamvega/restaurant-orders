package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Mesa;
import reactor.core.publisher.Mono;

/**
 * Use Case: Get a restaurant table by ID.
 */
public interface GetMesaUseCase {
    Mono<Mesa> execute(String mesaId);
}
