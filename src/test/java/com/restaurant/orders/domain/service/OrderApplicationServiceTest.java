package com.restaurant.orders.domain.service;

import com.restaurant.orders.adapters.ports.in.AddOrderItemCommand;
import com.restaurant.orders.adapters.ports.in.ListOrdersFilter;
import com.restaurant.orders.adapters.ports.in.dto.CreateOrderCommand;
import com.restaurant.orders.adapters.ports.in.dto.CreateOrderItemCommand;
import com.restaurant.orders.adapters.ports.in.dto.CreateOrderRequest;
import com.restaurant.orders.adapters.ports.in.dto.OrderItemRequest;
import com.restaurant.orders.adapters.ports.in.dto.OrderResponse;
import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderItem;
import com.restaurant.orders.domain.model.OrderStatus;
import com.restaurant.orders.infrastructure.exceptions.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @InjectMocks
    private OrderApplicationService orderService;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 2, new BigDecimal("45.00"))
        );
        sampleOrder = Order.of(5, items);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        CreateOrderItemCommand itemCmd = new CreateOrderItemCommand(
                "Tacos al Pastor", 2, new BigDecimal("45.00"));
        CreateOrderCommand command = new CreateOrderCommand(5, new CreateOrderItemCommand[]{itemCmd});

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId("generated-id");
            return Mono.just(order);
        });

        Mono<Order> result = orderService.createOrder(command);

        StepVerifier.create(result)
                .assertNext(order -> {
                    assertEquals(5, order.getTableNumber());
                    assertEquals(1, order.getItems().size());
                    assertEquals(new BigDecimal("90.00"), order.getTotalAmount());
                    assertEquals(OrderStatus.PENDING, order.getStatus());
                })
                .verifyComplete();

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldGetOrderById() {
        when(orderRepository.findById("order-1")).thenReturn(Mono.just(sampleOrder));

        Mono<Order> result = orderService.getOrder("order-1");

        StepVerifier.create(result)
                .assertNext(order -> {
                    assertEquals(5, order.getTableNumber());
                    assertEquals(OrderStatus.PENDING, order.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById("non-existent")).thenReturn(Mono.empty());

        Mono<Order> result = orderService.getOrder("non-existent");

        StepVerifier.create(result)
                .expectError(OrderNotFoundException.class)
                .verify();
    }

    @Test
    void shouldListAllOrders() {
        when(orderRepository.findAll(any(), any(), any(), any()))
                .thenReturn(Flux.just(sampleOrder));

        ListOrdersFilter filter = new ListOrdersFilter(null, java.util.Optional.empty(), null, null);
        Flux<Order> result = orderService.listOrders(filter);

        StepVerifier.create(result)
                .assertNext(order -> assertEquals(5, order.getTableNumber()))
                .verifyComplete();
    }

    @Test
    void shouldListOrdersByTable() {
        when(orderRepository.findAll(
                org.mockito.ArgumentMatchers.eq(java.util.Optional.of(5)),
                org.mockito.ArgumentMatchers.eq(java.util.Optional.empty()),
                org.mockito.ArgumentMatchers.eq(java.util.Optional.empty()),
                org.mockito.ArgumentMatchers.eq(java.util.Optional.empty())
        )).thenReturn(Flux.just(sampleOrder));

        ListOrdersFilter filter = new ListOrdersFilter(5, java.util.Optional.empty(), null, null);
        Flux<Order> result = orderService.listOrders(filter);

        StepVerifier.create(result)
                .assertNext(order -> assertEquals(5, order.getTableNumber()))
                .verifyComplete();
    }

    @Test
    void shouldListOrdersByStatus() {
        when(orderRepository.findAll(
                org.mockito.ArgumentMatchers.eq(java.util.Optional.empty()),
                org.mockito.ArgumentMatchers.eq(java.util.Optional.of(OrderStatus.PENDING)),
                org.mockito.ArgumentMatchers.eq(java.util.Optional.empty()),
                org.mockito.ArgumentMatchers.eq(java.util.Optional.empty())
        )).thenReturn(Flux.just(sampleOrder));

        ListOrdersFilter filter = new ListOrdersFilter(null, java.util.Optional.of("PENDING"), null, null);
        Flux<Order> result = orderService.listOrders(filter);

        StepVerifier.create(result)
                .assertNext(order -> assertEquals(OrderStatus.PENDING, order.getStatus()))
                .verifyComplete();
    }

    @Test
    void shouldUpdateOrderStatus() {
        when(orderRepository.findById("order-1")).thenReturn(Mono.just(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Order> result = orderService.updateStatus("order-1", OrderStatus.PREPARING);

        StepVerifier.create(result)
                .assertNext(order -> assertEquals(OrderStatus.PREPARING, order.getStatus()))
                .verifyComplete();
    }

    @Test
    void shouldNotAllowInvalidStatusTransition() {
        when(orderRepository.findById("order-1")).thenReturn(Mono.just(sampleOrder));

        Mono<Order> result = orderService.updateStatus("order-1", OrderStatus.DELIVERED);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void shouldCancelOrder() {
        when(orderRepository.findById("order-1")).thenReturn(Mono.just(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Order> result = orderService.deleteOrder("order-1");

        StepVerifier.create(result)
                .assertNext(order -> assertEquals(OrderStatus.CANCELLED, order.getStatus()))
                .verifyComplete();
    }

    @Test
    void shouldAddItemToOrder() {
        when(orderRepository.findById("order-1")).thenReturn(Mono.just(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        AddOrderItemCommand command = new AddOrderItemCommand("Cerveza", 4, new BigDecimal("35.00"));
        Mono<Order> result = orderService.addOrderItem("order-1", command);

        StepVerifier.create(result)
                .assertNext(order -> {
                    assertEquals(2, order.getItems().size());
                    assertEquals(new BigDecimal("230.00"), order.getTotalAmount());
                })
                .verifyComplete();
    }

    @Test
    void shouldRemoveItemFromOrder() {
        when(orderRepository.findById("order-1")).thenReturn(Mono.just(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Order> result = orderService.removeOrderItem("order-1", "Tacos al Pastor");

        StepVerifier.create(result)
                .assertNext(order -> {
                    assertEquals(0, order.getItems().size());
                    assertEquals(BigDecimal.ZERO, order.getTotalAmount());
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowWhenRemovingNonExistentItem() {
        when(orderRepository.findById("order-1")).thenReturn(Mono.just(sampleOrder));

        Mono<Order> result = orderService.removeOrderItem("order-1", "Pizza");

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void shouldConvertOrderToResponse() {
        sampleOrder.setId("order-1");
        OrderResponse response = orderService.toResponse(sampleOrder);

        assertEquals("order-1", response.getId());
        assertEquals(5, response.getTableNumber());
        assertEquals(1, response.getItems().size());
        assertEquals("Tacos al Pastor", response.getItems().get(0).getDishName());
        assertEquals(new BigDecimal("90.00"), response.getTotalAmount());
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void shouldConvertRequestToCommand() {
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setDishName("Tacos");
        itemReq.setQuantity(2);
        itemReq.setUnitPrice(new BigDecimal("45.00"));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setTableNumber(3);
        request.setItems(new OrderItemRequest[]{itemReq});

        CreateOrderCommand command = orderService.toCreateCommand(request);

        assertEquals(3, command.tableNumber());
        assertEquals(1, command.items().length);
        assertEquals("Tacos", command.items()[0].dishName());
    }
}
