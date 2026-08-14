package com.restaurant.orders.adapters.drivers.in.http;

import com.restaurant.orders.adapters.ports.in.*;
import com.restaurant.orders.adapters.ports.in.dto.*;
import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderStatus;
import com.restaurant.orders.domain.service.OrderApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;

/**
 * WebFlux Functional Endpoint for order management.
 * Uses RouterFunction-based routing for a purely reactive approach.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEndpoints {

    private final OrderApplicationService orderService;

    /**
     * POST /api/orders - Create a new order.
     */
    public Mono<ServerResponse> createOrder(ServerRequest request) {
        Mono<CreateOrderRequest> body = request.bodyToMono(CreateOrderRequest.class);

        return body
                .doOnNext(req -> log.info("Received create order request for table {}", req.getTableNumber()))
                .flatMap(orderService::toCreateCommand)
                .flatMap(orderService::createOrder)
                .map(orderService::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    /**
     * GET /api/orders/{id} - Get order by ID.
     */
    public Mono<ServerResponse> getOrder(ServerRequest request) {
        String orderId = request.pathVariable("id");

        return orderService.getOrder(orderId)
                .map(orderService::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    /**
     * GET /api/orders - List orders with optional filters.
     */
    public Mono<ServerResponse> listOrders(ServerRequest request) {
        Integer tableNumber = getIntegerParam(request, "table");
        String status = request.queryParam("status").orElse(null);
        LocalDateTime dateFrom = getLocalDateTimeParam(request, "dateFrom");
        LocalDateTime dateTo = getLocalDateTimeParam(request, "dateTo");

        ListOrdersFilter filter = orderService.toFilter(tableNumber, status, dateFrom, dateTo);

        Flux<OrderResponse> response = orderService.listOrders(filter)
                .map(orderService::toResponse);

        return ServerResponse.ok().body(response, OrderResponse.class);
    }

    /**
     * PATCH /api/orders/{id}/status - Update order status.
     */
    public Mono<ServerResponse> updateStatus(ServerRequest request) {
        String orderId = request.pathVariable("id");
        Mono<UpdateStatusRequest> body = request.bodyToMono(UpdateStatusRequest.class);

        return body
                .doOnNext(req -> log.info("Updating order {} status to {}", orderId, req.getStatus()))
                .map(UpdateStatusRequest::getStatus)
                .map(OrderStatus::valueOf)
                .flatMap(newStatus -> orderService.updateStatus(orderId, newStatus))
                .map(orderService::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    /**
     * DELETE /api/orders/{id} - Cancel/delete an order.
     */
    public Mono<ServerResponse> deleteOrder(ServerRequest request) {
        String orderId = request.pathVariable("id");

        return orderService.deleteOrder(orderId)
                .map(orderService::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    /**
     * POST /api/orders/{id}/items - Add item to order.
     */
    public Mono<ServerResponse> addOrderItem(ServerRequest request) {
        String orderId = request.pathVariable("id");
        Mono<AddItemRequest> body = request.bodyToMono(AddItemRequest.class);

        return body
                .doOnNext(req -> log.info("Adding item to order {}: {}", orderId, req.getDishName()))
                .map(req -> new AddOrderItemCommand(
                        req.getDishName(),
                        req.getQuantity(),
                        req.getUnitPrice()
                ))
                .flatMap(cmd -> orderService.addOrderItem(orderId, cmd))
                .map(orderService::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    /**
     * DELETE /api/orders/{id}/items/{dishName} - Remove item from order.
     */
    public Mono<ServerResponse> removeOrderItem(ServerRequest request) {
        String orderId = request.pathVariable("id");
        String dishName = request.pathVariable("dishName");

        return orderService.removeOrderItem(orderId, dishName)
                .then(ServerResponse.noContent().build());
    }

    // ==================== Helper Methods ====================

    private Integer getIntegerParam(ServerRequest request, String param) {
        return request.queryParam(param)
                .map(Integer::parseInt)
                .orElse(null);
    }

    private LocalDateTime getLocalDateTimeParam(ServerRequest request, String param) {
        return request.queryParam(param)
                .map(value -> {
                    try {
                        return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException(
                                "Invalid date format for " + param + ". Use ISO format: yyyy-MM-ddTHH:mm:ss");
                    }
                })
                .orElse(null);
    }
}
