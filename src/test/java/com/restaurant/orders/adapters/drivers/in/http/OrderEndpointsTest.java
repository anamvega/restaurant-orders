package com.restaurant.orders.adapters.drivers.in.http;

import com.restaurant.orders.adapters.ports.in.AddOrderItemCommand;
import com.restaurant.orders.adapters.ports.in.ListOrdersFilter;
import com.restaurant.orders.adapters.ports.in.dto.CreateOrderCommand;
import com.restaurant.orders.adapters.ports.in.dto.CreateOrderItemCommand;
import com.restaurant.orders.adapters.ports.in.dto.CreateOrderRequest;
import com.restaurant.orders.adapters.ports.in.dto.OrderItemRequest;
import com.restaurant.orders.adapters.ports.in.dto.OrderResponse;
import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderItem;
import com.restaurant.orders.domain.model.OrderStatus;
import com.restaurant.orders.domain.service.OrderApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderEndpointsTest {

    @Mock
    private OrderApplicationService orderService;

    private WebTestClient client;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = Order.of(5, List.of(
                OrderItem.of("Tacos al Pastor", 2, new BigDecimal("45.00"))
        ));
        sampleOrder.setId("order-1");

        OrderEndpoints endpoints = new OrderEndpoints(orderService);
        OrderRouter router = new OrderRouter();
        RouterFunction<org.springframework.web.reactive.function.server.ServerResponse> route =
                router.orderRoutes(endpoints);

        client = WebTestClient.bindToRouterFunction(route).build();
    }

    @Test
    void shouldCreateOrder() {
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setDishName("Tacos al Pastor");
        itemReq.setQuantity(2);
        itemReq.setUnitPrice(new BigDecimal("45.00"));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setTableNumber(5);
        request.setItems(new OrderItemRequest[]{itemReq});

        CreateOrderCommand command = new CreateOrderCommand(5,
                new CreateOrderItemCommand[]{new CreateOrderItemCommand("Tacos al Pastor", 2, new BigDecimal("45.00"))});

        when(orderService.toCreateCommand(request)).thenReturn(command);
        when(orderService.createOrder(command)).thenReturn(Mono.just(sampleOrder));

        OrderResponse response = OrderResponse.builder()
                .id("order-1")
                .tableNumber(5)
                .status("PENDING")
                .totalAmount(new BigDecimal("90.00"))
                .items(List.of(
                        OrderResponse.OrderItemResponse.builder()
                                .dishName("Tacos al Pastor")
                                .quantity(2)
                                .unitPrice(new BigDecimal("45.00"))
                                .subtotal(new BigDecimal("90.00"))
                                .build()
                ))
                .build();
        when(orderService.toResponse(sampleOrder)).thenReturn(response);

        client.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.tableNumber").isEqualTo(5)
                .jsonPath("$.items[0].dishName").isEqualTo("Tacos al Pastor")
                .jsonPath("$.totalAmount").isEqualTo(90.00)
                .jsonPath("$.status").isEqualTo("PENDING");
    }

    @Test
    void shouldGetOrderById() {
        when(orderService.getOrder("order-1")).thenReturn(Mono.just(sampleOrder));
        OrderResponse response = OrderResponse.builder()
                .id("order-1")
                .tableNumber(5)
                .status("PENDING")
                .totalAmount(new BigDecimal("90.00"))
                .items(List.of())
                .build();
        when(orderService.toResponse(sampleOrder)).thenReturn(response);

        client.get().uri("/api/orders/order-1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("order-1")
                .jsonPath("$.tableNumber").isEqualTo(5)
                .jsonPath("$.status").isEqualTo("PENDING");
    }

    @Test
    void shouldListOrdersWithFilters() {
        ListOrdersFilter filter = new ListOrdersFilter(5, Optional.of("PENDING"), null, null);
        when(orderService.toFilter(eq(5), eq("PENDING"), eq(null), eq(null))).thenReturn(filter);
        when(orderService.listOrders(filter)).thenReturn(Flux.just(sampleOrder));

        OrderResponse response = OrderResponse.builder()
                .id("order-1")
                .tableNumber(5)
                .status("PENDING")
                .totalAmount(new BigDecimal("90.00"))
                .items(List.of())
                .build();
        when(orderService.toResponse(sampleOrder)).thenReturn(response);

        client.get().uri("/api/orders?table=5&status=PENDING")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("order-1");
    }

    @Test
    void shouldUpdateOrderStatus() {
        when(orderService.updateStatus(eq("order-1"), eq(OrderStatus.PREPARING)))
                .thenReturn(Mono.just(sampleOrder));

        OrderResponse response = OrderResponse.builder()
                .id("order-1")
                .tableNumber(5)
                .status("PREPARING")
                .totalAmount(new BigDecimal("90.00"))
                .items(List.of())
                .build();
        when(orderService.toResponse(sampleOrder)).thenReturn(response);

        client.patch().uri("/api/orders/order-1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(java.util.Map.of("status", "PREPARING"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("PREPARING");
    }

    @Test
    void shouldDeleteOrder() {
        when(orderService.deleteOrder("order-1")).thenReturn(Mono.just(sampleOrder));

        OrderResponse response = OrderResponse.builder()
                .id("order-1")
                .tableNumber(5)
                .status("CANCELLED")
                .totalAmount(new BigDecimal("90.00"))
                .items(List.of())
                .build();
        when(orderService.toResponse(sampleOrder)).thenReturn(response);

        client.delete().uri("/api/orders/order-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("CANCELLED");
    }

    @Test
    void shouldAddItemToOrder() {
        when(orderService.addOrderItem(eq("order-1"), any(AddOrderItemCommand.class)))
                .thenReturn(Mono.just(sampleOrder));

        OrderResponse response = OrderResponse.builder()
                .id("order-1")
                .tableNumber(5)
                .status("PENDING")
                .totalAmount(new BigDecimal("230.00"))
                .items(List.of())
                .build();
        when(orderService.toResponse(sampleOrder)).thenReturn(response);

        client.post().uri("/api/orders/order-1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(java.util.Map.of("dishName", "Cerveza", "quantity", 4, "unitPrice", 35.00))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalAmount").isEqualTo(230.00);

        ArgumentCaptor<AddOrderItemCommand> captor = ArgumentCaptor.forClass(AddOrderItemCommand.class);
        verify(orderService).addOrderItem(eq("order-1"), captor.capture());
        assertEquals("Cerveza", captor.getValue().dishName());
        assertEquals(4, captor.getValue().quantity());
    }

    @Test
    void shouldRemoveItemFromOrder() {
        when(orderService.removeOrderItem("order-1", "Tacos")).thenReturn(Mono.just(sampleOrder));

        client.delete().uri("/api/orders/order-1/items/Tacos")
                .exchange()
                .expectStatus().isNoContent();

        verify(orderService).removeOrderItem("order-1", "Tacos");
    }
}
