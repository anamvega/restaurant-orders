package com.restaurant.orders.domain.repository;

import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Repository interface for Order aggregate.
 * Uses Spring Data MongoDB reactive driver.
 */
@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    /**
     * Find orders by table number.
     */
    Flux<Order> findByTableNumber(Integer tableNumber);

    /**
     * Find orders by status.
     */
    Flux<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by table number and status.
     */
    Flux<Order> findByTableNumberAndStatus(Integer tableNumber, OrderStatus status);

    /**
     * Find orders within a date range (createdAt).
     */
    Flux<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find orders by table number within a date range.
     */
    Flux<Order> findByTableNumberAndCreatedAtBetween(Integer tableNumber, LocalDateTime start, LocalDateTime end);

    /**
     * Find orders by status within a date range.
     */
    Flux<Order> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);

    /**
     * Find orders by table number, status, and date range.
     */
    Flux<Order> findByTableNumberAndStatusAndCreatedAtBetween(
            Integer tableNumber, OrderStatus status, LocalDateTime start, LocalDateTime end);

    /**
     * Count orders by table number.
     */
    Long countByTableNumber(Integer tableNumber);

    /**
     * Count orders by status.
     */
    Long countByStatus(OrderStatus status);
}
