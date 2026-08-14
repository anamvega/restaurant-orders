package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.UpdateOrderStatusUseCase;
import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the UpdateOrderStatusUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateOrderStatusUseCaseImpl implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Mono<Order> execute(String orderId, OrderStatus newStatus) {
        log.info("Executing UpdateOrderStatusUseCase for order {} to status {}", orderId, newStatus);

        return orderRepository.findById(orderId)
                .flatMap(order -> {
                    order.updateStatus(newStatus);
                    return orderRepository.save(order);
                });
    }
}
