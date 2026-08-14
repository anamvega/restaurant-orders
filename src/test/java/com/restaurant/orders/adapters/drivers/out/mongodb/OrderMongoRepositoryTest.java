package com.restaurant.orders.adapters.drivers.out.mongodb;

import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderItem;
import com.restaurant.orders.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class OrderMongoRepositoryTest {

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    private OrderMongoRepository repository;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        repository = new OrderMongoRepository(mongoTemplate);

        List<OrderItem> items = List.of(
                OrderItem.of("Tacos al Pastor", 2, new BigDecimal("45.00"))
        );
        sampleOrder = Order.of(5, items);
        sampleOrder.setId("order-1");
    }

    @Test
    void shouldSaveOrder() {
        when(mongoTemplate.save(any(Order.class))).thenReturn(Mono.just(sampleOrder));

        Mono<Order> result = repository.save(sampleOrder);

        StepVerifier.create(result)
                .assertNext(order -> assertEquals("order-1", order.getId()))
                .verifyComplete();
    }

    @Test
    void shouldFindById() {
        when(mongoTemplate.findById(eq("order-1"), eq(Order.class)))
                .thenReturn(Mono.just(sampleOrder));

        Mono<Order> result = repository.findById("order-1");

        StepVerifier.create(result)
                .assertNext(order -> assertEquals(5, order.getTableNumber()))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        when(mongoTemplate.findById(eq("non-existent"), eq(Order.class)))
                .thenReturn(Mono.empty());

        Mono<Order> result = repository.findById("non-existent");

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldDeleteById() {
        when(mongoTemplate.remove(any(org.springframework.data.mongodb.core.query.Query.class), eq(Order.class)))
                .thenReturn(Mono.empty());

        Mono<Void> result = repository.deleteById("order-1");

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldFindAllWithFilters() {
        when(mongoTemplate.find(any(), eq(Order.class)))
                .thenReturn(Flux.just(sampleOrder));

        Flux<Order> result = repository.findAll(
                Optional.of(5),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        StepVerifier.create(result)
                .assertNext(order -> assertEquals(5, order.getTableNumber()))
                .verifyComplete();
    }

    @Test
    void shouldCheckExistence() {
        when(mongoTemplate.exists(any(), eq(Order.class))).thenReturn(Mono.just(true));

        Mono<Boolean> result = repository.existsById("order-1");

        StepVerifier.create(result)
                .assertNext(exists -> assertTrue(exists))
                .verifyComplete();
    }
}
