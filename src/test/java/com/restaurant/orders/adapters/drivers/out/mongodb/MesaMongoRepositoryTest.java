package com.restaurant.orders.adapters.drivers.out.mongodb;

import com.restaurant.orders.domain.model.Mesa;
import com.restaurant.orders.domain.model.MesaStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MesaMongoRepositoryTest {

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    private MesaMongoRepository repository;

    private Mesa sampleMesa;

    @BeforeEach
    void setUp() {
        repository = new MesaMongoRepository(mongoTemplate);
        sampleMesa = Mesa.builder().id("mesa-1").number(5).capacity(4).build();
    }

    @Test
    void shouldSaveMesa() {
        when(mongoTemplate.save(any(Mesa.class))).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));

        StepVerifier.create(repository.save(sampleMesa))
                .assertNext(mesa -> assertEquals(5, mesa.getNumber()))
                .verifyComplete();

        verify(mongoTemplate).save(sampleMesa);
    }

    @Test
    void shouldFindMesaById() {
        when(mongoTemplate.findById("mesa-1", Mesa.class)).thenReturn(Mono.just(sampleMesa));

        StepVerifier.create(repository.findById("mesa-1"))
                .assertNext(mesa -> assertEquals(5, mesa.getNumber()))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenMesaNotFound() {
        when(mongoTemplate.findById("missing", Mesa.class)).thenReturn(Mono.empty());

        StepVerifier.create(repository.findById("missing"))
                .verifyComplete();
    }

    @Test
    void shouldFindMesaByNumber() {
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        when(mongoTemplate.findOne(any(Query.class), eq(Mesa.class))).thenReturn(Mono.just(sampleMesa));

        StepVerifier.create(repository.findByNumber(5))
                .assertNext(mesa -> assertEquals(5, mesa.getNumber()))
                .verifyComplete();

        verify(mongoTemplate).findOne(queryCaptor.capture(), eq(Mesa.class));
        assertEquals(5, queryCaptor.getValue().getQueryObject().get("number"));
    }

    @Test
    void shouldDeleteMesaById() {
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        when(mongoTemplate.remove(any(Query.class), eq(Mesa.class))).thenReturn(Mono.empty());

        StepVerifier.create(repository.deleteById("mesa-1"))
                .verifyComplete();

        verify(mongoTemplate).remove(queryCaptor.capture(), eq(Mesa.class));
        assertEquals("mesa-1", queryCaptor.getValue().getQueryObject().get("_id"));
    }

    @Test
    void shouldFindAllMesasWithoutFilter() {
        when(mongoTemplate.find(any(Query.class), eq(Mesa.class))).thenReturn(Flux.just(sampleMesa));

        StepVerifier.create(repository.findAll(Optional.empty()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldFindMesasFilteredByStatus() {
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        when(mongoTemplate.find(any(Query.class), eq(Mesa.class))).thenReturn(Flux.just(sampleMesa));

        StepVerifier.create(repository.findAll(Optional.of(MesaStatus.OCCUPIED)))
                .expectNextCount(1)
                .verifyComplete();

        verify(mongoTemplate).find(queryCaptor.capture(), eq(Mesa.class));
        assertEquals(MesaStatus.OCCUPIED, queryCaptor.getValue().getQueryObject().get("status"));
    }

    @Test
    void shouldFindAvailableMesas() {
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        when(mongoTemplate.find(any(Query.class), eq(Mesa.class))).thenReturn(Flux.just(sampleMesa));

        StepVerifier.create(repository.findAvailable())
                .expectNextCount(1)
                .verifyComplete();

        verify(mongoTemplate).find(queryCaptor.capture(), eq(Mesa.class));
        assertEquals(MesaStatus.AVAILABLE, queryCaptor.getValue().getQueryObject().get("status"));
    }

    @Test
    void shouldCheckIfMesaExistsByNumber() {
        when(mongoTemplate.exists(any(Query.class), eq(Mesa.class))).thenReturn(Mono.just(true));

        StepVerifier.create(repository.existsByNumber(5))
                .assertNext(exists -> assertTrue(exists))
                .verifyComplete();
    }

    @Test
    void shouldCountMesasByStatus() {
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        when(mongoTemplate.count(any(Query.class), eq(Mesa.class))).thenReturn(Mono.just(3L));

        StepVerifier.create(repository.countByStatus(MesaStatus.AVAILABLE))
                .assertNext(count -> assertEquals(3L, count))
                .verifyComplete();

        verify(mongoTemplate).count(queryCaptor.capture(), eq(Mesa.class));
        assertEquals(MesaStatus.AVAILABLE, queryCaptor.getValue().getQueryObject().get("status"));
    }
}
