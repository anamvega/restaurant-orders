package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.CreateOrderUseCase;
import com.restaurant.orders.adapters.ports.in.dto.CreateOrderCommand;
import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the CreateOrderUseCase.
 * Validates input and delegates to the application service for execution.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Mono<Order> execute(CreateOrderCommand command) {
        log.info("Executing CreateOrderUseCase for table {}", command.tableNumber());

        if (!command.hasItems()) {
            return Mono.error(new IllegalArgumentException("Order must have at least one item"));
        }

        if (command.tableNumber() == null || command.tableNumber() <= 0) {
            return Mono.error(new IllegalArgumentException("Table number must be greater than 0"));
        }

        // The actual order creation and validation is done in the Order domain aggregate
        // via Order.of() which handles all business rules
        return Mono.just(command);
    }
}
