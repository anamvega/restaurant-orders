package com.restaurant.orders.adapters.drivers.out.mongodb;

import com.restaurant.orders.adapters.ports.out.MesaRepositoryPort;
import com.restaurant.orders.domain.model.Mesa;
import com.restaurant.orders.domain.model.MesaStatus;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * MongoDB implementation of the MesaRepositoryPort.
 * This is the concrete adapter that connects the domain to MongoDB.
 */
@Component
public class MesaMongoRepository implements MesaRepositoryPort {

    private final ReactiveMongoTemplate mongoTemplate;

    public MesaMongoRepository(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Mesa> save(Mesa mesa) {
        return mongoTemplate.save(mesa);
    }

    @Override
    public Mono<Mesa> findById(String mesaId) {
        return mongoTemplate.findById(mesaId, Mesa.class);
    }

    @Override
    public Mono<Mesa> findByNumber(Integer number) {
        Query query = new Query(Criteria.where("number").is(number));
        return mongoTemplate.findOne(query, Mesa.class);
    }

    @Override
    public Mono<Void> deleteById(String mesaId) {
        Query query = new Query(Criteria.where("_id").is(mesaId));
        return mongoTemplate.remove(query, Mesa.class).then();
    }

    @Override
    public Flux<Mesa> findAll(Optional<MesaStatus> status) {
        Query query = new Query();

        status.ifPresent(statusValue ->
            query.addCriteria(Criteria.where("status").is(statusValue)));

        return mongoTemplate.find(query, Mesa.class);
    }

    @Override
    public Flux<Mesa> findAvailable() {
        Query query = new Query(Criteria.where("status").is(MesaStatus.AVAILABLE));
        return mongoTemplate.find(query, Mesa.class);
    }

    @Override
    public Mono<Boolean> existsByNumber(Integer number) {
        Query query = new Query(Criteria.where("number").is(number));
        return mongoTemplate.exists(query, Mesa.class);
    }

    @Override
    public Mono<Long> countByStatus(MesaStatus status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.count(query, Mesa.class);
    }
}
