package com.restaurant.orders.adapters.ports.in;

import reactor.core.publisher.Mono;

/**
 * Use Case: Delete a restaurant table.
 */
public interface DeleteMesaUseCase {
    Mono<Void> execute(String mesaId);
}
