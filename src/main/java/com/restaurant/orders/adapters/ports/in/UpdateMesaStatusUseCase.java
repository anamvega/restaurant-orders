package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Mesa;
import reactor.core.publisher.Mono;

/**
 * Use Case: Update the status of a restaurant table.
 */
public interface UpdateMesaStatusUseCase {
    Mono<Mesa> execute(String mesaId, String status);
}
