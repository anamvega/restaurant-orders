package com.restaurant.orders.adapters.drivers.out.mongodb;

import com.restaurant.orders.adapters.ports.out.OrderRepositoryPort;
import com.restaurant.orders.domain.model.Order;
import com.restaurant.orders.domain.model.OrderStatus;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * MongoDB implementation of the OrderRepositoryPort.
 * This is the concrete adapter that connects the domain to MongoDB.
 */
@Component
public class OrderMongoRepository implements OrderRepositoryPort {

    private final ReactiveMongoTemplate mongoTemplate;

    public OrderMongoRepository(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Order> save(Order order) {
        return mongoTemplate.save(order);
    }

    @Override
    public Mono<Order> findById(String orderId) {
        return mongoTemplate.findById(orderId, Order.class);
    }

    @Override
    public Mono<Void> deleteById(String orderId) {
        Query query = new Query(Criteria.where("_id").is(orderId));
        return mongoTemplate.remove(query, Order.class).then();
    }

    @Override
    public Flux<Order> findAll(Optional<Integer> tableNumber,
                                Optional<OrderStatus> status,
                                Optional<LocalDateTime> dateFrom,
                                Optional<LocalDateTime> dateTo) {
        Query query = new Query();

        tableNumber.ifPresent(table -> 
            query.addCriteria(Criteria.where("tableNumber").is(table)));

        status.ifPresent(statusValue -> 
            query.addCriteria(Criteria.where("status").is(statusValue)));

        dateFrom.ifPresent(from -> 
            query.addCriteria(Criteria.where("createdAt").gte(from)));

        dateTo.ifPresent(to -> 
            query.addCriteria(Criteria.where("createdAt").lte(to)));

        return mongoTemplate.find(query, Order.class);
    }

    @Override
    public Mono<Boolean> existsById(String orderId) {
        return mongoTemplate.exists(new Query(Criteria.where("_id").is(orderId)), Order.class);
    }
}
