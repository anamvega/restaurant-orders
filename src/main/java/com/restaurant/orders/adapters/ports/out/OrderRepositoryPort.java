package com.restaurant.orders.adapters.ports.out;

import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Output port: Repository interface for Order aggregate.
 * This is the hexagonal architecture outbound port that defines
 * how the domain layer interacts with external persistence.
 */
public interface OrderRepositoryPort {

    /**
     * Save a new order or update an existing one.
     */
    Mono<Order> save(Order order);

    /**
     * Find an order by its ID.
     */
    Mono<Order> findById(String orderId);

    /**
     * Delete an order by its ID.
     */
    Mono<Void> deleteById(String orderId);

    /**
     * List all orders with optional filters.
     */
    Flux<Order> findAll(Optional<Integer> tableNumber,
                        Optional<OrderStatus> status,
                        Optional<LocalDateTime> dateFrom,
                        Optional<LocalDateTime> dateTo);

    /**
     * Check if an order exists by ID.
     */
    Mono<Boolean> existsById(String orderId);
}
