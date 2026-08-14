package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.RemoveOrderItemUseCase;
import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the RemoveOrderItemUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RemoveOrderItemUseCaseImpl implements RemoveOrderItemUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Mono<Order> execute(String orderId, String dishName) {
        log.info("Executing RemoveOrderItemUseCase for order {}: {}", orderId, dishName);

        if (dishName == null || dishName.isBlank()) {
            return Mono.error(new IllegalArgumentException("Dish name cannot be empty"));
        }

        return orderRepository.findById(orderId)
                .flatMap(order -> {
                    boolean removed = order.removeItem(dishName);
                    if (!removed) {
                        return Mono.error(new IllegalArgumentException(
                                "Item not found: " + dishName));
                    }
                    return orderRepository.save(order);
                });
    }
}
