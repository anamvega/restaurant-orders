package com.restaurant.orders.adapters.ports.in;

import com.restaurant.orders.domain.model.Mesa;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use Case: Get all restaurant tables with optional status filter.
 */
public interface ListMesaUseCase {
    Flux<Mesa> execute(String status);
}
