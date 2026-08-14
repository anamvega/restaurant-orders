package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.GetOrderUseCase;
import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the GetOrderUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Mono<Order> execute(String orderId) {
        log.info("Executing GetOrderUseCase for ID: {}", orderId);
        return orderRepository.findById(orderId);
    }
}
