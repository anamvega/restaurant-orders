package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.DeleteOrderUseCase;
import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the DeleteOrderUseCase.
 * Cancels the order by transitioning its status to CANCELLED.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteOrderUseCaseImpl implements DeleteOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Mono<Order> execute(String orderId) {
        log.info("Executing DeleteOrderUseCase for ID: {}", orderId);

        return orderRepository.findById(orderId)
                .flatMap(order -> {
                    order.updateStatus(OrderStatus.CANCELLED);
                    return orderRepository.save(order);
                });
    }
}
