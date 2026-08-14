package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.ListOrdersFilter;
import com.restaurant.orders.adapters.ports.in.ListOrdersUseCase;
import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of the ListOrdersUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListOrdersUseCaseImpl implements ListOrdersUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Flux<Order> execute(ListOrdersFilter filter) {
        log.info("Executing ListOrdersUseCase with filter: {}", filter);

        Optional<Integer> tableNum = Optional.ofNullable(filter.tableNumber());
        Optional<OrderStatus> statusOpt = Optional.empty();

        if (filter.status() != null && !filter.status().isBlank()) {
            try {
                statusOpt = Optional.of(OrderStatus.valueOf(filter.status().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + filter.status());
            }
        }

        return orderRepository.findAll(
                tableNum,
                statusOpt,
                Optional.ofNullable(filter.dateFrom()),
                Optional.ofNullable(filter.dateTo())
        );
    }
}
