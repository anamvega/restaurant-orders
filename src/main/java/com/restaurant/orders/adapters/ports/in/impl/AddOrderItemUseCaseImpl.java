package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.AddOrderItemCommand;
import com.restaurant.orders.adapters.ports.in.AddOrderItemUseCase;
import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the AddOrderItemUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AddOrderItemUseCaseImpl implements AddOrderItemUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Mono<Order> execute(String orderId, AddOrderItemCommand command) {
        log.info("Executing AddOrderItemUseCase for order {}: {}", orderId, command.dishName());

        if (command.quantity() == null || command.quantity() <= 0) {
            return Mono.error(new IllegalArgumentException("Quantity must be greater than 0"));
        }

        if (command.unitPrice() == null || command.unitPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("Unit price must be greater than 0"));
        }

        OrderItem newItem = OrderItem.of(command.dishName(), command.quantity(), command.unitPrice());

        return orderRepository.findById(orderId)
                .flatMap(order -> {
                    order.addItem(newItem);
                    return orderRepository.save(order);
                });
    }
}
