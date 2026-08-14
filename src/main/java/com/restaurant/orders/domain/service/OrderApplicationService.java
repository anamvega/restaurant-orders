package com.restaurant.orders.domain.service;

import com.restaurant.orders.adapters.ports.in.*;
import com.restaurant.orders.adapters.ports.in.dto.*;
import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderItem;
import com.restaurant.orders.domain.model.OrderStatus;
import com.restaurant.orders.infrastructure.exceptions.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Application Service that orchestrates all use cases for order management.
 * This service acts as the facade between the HTTP adapters and the domain layer.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderApplicationService {

    private final OrderRepositoryPort orderRepository;

    // ==================== Create Order ====================

    @Transactional
    public Mono<Order> createOrder(CreateOrderCommand command) {
        log.info("Creating order for table {}", command.tableNumber());

        OrderItem[] items = Arrays.stream(command.items())
                .map(item -> OrderItem.of(
                        item.dishName(),
                        item.quantity(),
                        item.unitPrice()
                ))
                .toArray(OrderItem[]::new);

        Order order = Order.of(command.tableNumber(), List.of(items));
        log.info("Order created with ID: {}", order.getId());
        return orderRepository.save(order);
    }

    // ==================== Get Order ====================

    public Mono<Order> getOrder(String orderId) {
        log.info("Fetching order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)));
    }

    // ==================== List Orders ====================

    public Flux<Order> listOrders(ListOrdersFilter filter) {
        log.info("Listing orders with filters: table={}, status={}, dateFrom={}, dateTo={}",
                filter.tableNumber(), filter.status(), filter.dateFrom(), filter.dateTo());

        Optional<Integer> tableNum = Optional.ofNullable(filter.tableNumber());
        Optional<OrderStatus> statusOpt = filter.status()
                .map(statusStr -> {
                    try {
                        return Optional.of(OrderStatus.valueOf(statusStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid status: " + statusStr);
                    }
                })
                .orElse(Optional.empty());

        return orderRepository.findAll(
                tableNum,
                statusOpt,
                Optional.ofNullable(filter.dateFrom()),
                Optional.ofNullable(filter.dateTo())
        );
    }

    // ==================== Update Status ====================

    @Transactional
    public Mono<Order> updateStatus(String orderId, OrderStatus newStatus) {
        log.info("Updating order {} status to {}", orderId, newStatus);
        
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)))
                .flatMap(order -> {
                    order.updateStatus(newStatus);
                    return orderRepository.save(order);
                });
    }

    // ==================== Delete Order ====================

    @Transactional
    public Mono<Order> deleteOrder(String orderId) {
        log.info("Deleting order with ID: {}", orderId);
        
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)))
                .flatMap(order -> {
                    order.updateStatus(OrderStatus.CANCELLED);
                    return orderRepository.save(order)
                            .doOnCancel(() -> log.info("Order {} cancelled", orderId));
                });
    }

    // ==================== Add Item ====================

    @Transactional
    public Mono<Order> addOrderItem(String orderId, AddOrderItemCommand command) {
        log.info("Adding item to order {}: {} x {}", orderId, command.quantity(), command.dishName());

        OrderItem newItem = OrderItem.of(
                command.dishName(),
                command.quantity(),
                command.unitPrice()
        );

        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)))
                .flatMap(order -> {
                    order.addItem(newItem);
                    return orderRepository.save(order);
                });
    }

    // ==================== Remove Item ====================

    @Transactional
    public Mono<Order> removeOrderItem(String orderId, String dishName) {
        log.info("Removing item '{}' from order {}", dishName, orderId);

        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)))
                .flatMap(order -> {
                    boolean removed = order.removeItem(dishName);
                    if (!removed) {
                        return Mono.error(new IllegalArgumentException(
                                "Item not found: " + dishName));
                    }
                    return orderRepository.save(order);
                });
    }

    // ==================== DTO Conversion ====================

    /**
     * Convert domain Order to response DTO.
     */
    public OrderResponse toResponse(Order order) {
        var items = order.getItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .dishName(item.getDishName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .tableNumber(order.getTableNumber())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /**
     * Convert CreateOrderRequest to CreateOrderCommand.
     */
    public CreateOrderCommand toCreateCommand(CreateOrderRequest request) {
        var commands = Arrays.stream(request.getItems())
                .map(item -> new CreateOrderItemCommand(
                        item.getDishName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .toArray(CreateOrderItemCommand[]::new);

        return new CreateOrderCommand(request.getTableNumber(), commands);
    }

    /**
     * Convert ListOrdersFilter query params to filter object.
     */
    public ListOrdersFilter toFilter(Integer tableNumber, String status,
                                     LocalDateTime dateFrom, LocalDateTime dateTo) {
        return new ListOrdersFilter(tableNumber, Optional.ofNullable(status), dateFrom, dateTo);
    }
}
