package com.restaurant.orders.adapters.ports.out;

import com.restaurant.orders.domain.model.Mesa;
import com.restaurant.orders.domain.model.MesaStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Output port: Repository interface for Mesa aggregate.
 * This is the hexagonal architecture outbound port that defines
 * how the domain layer interacts with external persistence.
 */
public interface MesaRepositoryPort {

    /**
     * Save a new mesa or update an existing one.
     */
    Mono<Mesa> save(Mesa mesa);

    /**
     * Find a mesa by its ID.
     */
    Mono<Mesa> findById(String mesaId);

    /**
     * Find a mesa by its table number.
     */
    Mono<Mesa> findByNumber(Integer number);

    /**
     * Delete a mesa by its ID.
     */
    Mono<Void> deleteById(String mesaId);

    /**
     * List all mesas with optional status filter.
     */
    Flux<Mesa> findAll(Optional<MesaStatus> status);

    /**
     * List all available mesas.
     */
    Flux<Mesa> findAvailable();

    /**
     * Check if a mesa exists by its number.
     */
    Mono<Boolean> existsByNumber(Integer number);

    /**
     * Count mesas by status.
     */
    Mono<Long> countByStatus(MesaStatus status);
}
