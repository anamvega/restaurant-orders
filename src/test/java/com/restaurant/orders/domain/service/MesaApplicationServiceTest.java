package com.restaurant.orders.domain.service;

import com.restaurant.orders.adapters.ports.out.MesaRepositoryPort;
import com.restaurant.orders.domain.model.Mesa;
import com.restaurant.orders.domain.model.MesaStatus;
import com.restaurant.orders.infrastructure.exceptions.MesaAlreadyExistsException;
import com.restaurant.orders.infrastructure.exceptions.MesaNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MesaApplicationServiceTest {

    @Mock
    private MesaRepositoryPort mesaRepository;

    @InjectMocks
    private MesaApplicationService mesaService;

    private Mesa sampleMesa;

    @BeforeEach
    void setUp() {
        sampleMesa = Mesa.builder().id("mesa-1").number(5).capacity(4).build();
    }

    @Test
    void shouldCreateMesaWithDefaults() {
        when(mesaRepository.existsByNumber(5)).thenReturn(Mono.just(false));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(invocation -> {
            Mesa mesa = invocation.getArgument(0);
            mesa.setId("generated-id");
            return Mono.just(mesa);
        });

        Mono<Mesa> result = mesaService.createMesa(5, 4);

        StepVerifier.create(result)
                .assertNext(mesa -> {
                    assertEquals(5, mesa.getNumber());
                    assertEquals(4, mesa.getCapacity());
                    assertEquals(MesaStatus.AVAILABLE, mesa.getStatus());
                    assertNotNull(mesa.getCreatedAt());
                })
                .verifyComplete();

        verify(mesaRepository, times(1)).save(any(Mesa.class));
    }

    @Test
    void shouldRejectMesaWithDuplicateNumber() {
        when(mesaRepository.existsByNumber(5)).thenReturn(Mono.just(true));

        StepVerifier.create(mesaService.createMesa(5, 4))
                .expectError(MesaAlreadyExistsException.class)
                .verify();

        verify(mesaRepository, never()).save(any(Mesa.class));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void shouldRejectMesaWithInvalidNumber(Integer number) {
        StepVerifier.create(mesaService.createMesa(number, 4))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Table number must be greater than 0"))
                .verify();

        verifyNoInteractions(mesaRepository);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -3})
    void shouldRejectMesaWithInvalidCapacity(Integer capacity) {
        StepVerifier.create(mesaService.createMesa(5, capacity))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().equals("Capacity must be greater than 0"))
                .verify();

        verifyNoInteractions(mesaRepository);
    }

    @Test
    void shouldGetMesaById() {
        when(mesaRepository.findById("mesa-1")).thenReturn(Mono.just(sampleMesa));

        StepVerifier.create(mesaService.getMesa("mesa-1"))
                .assertNext(mesa -> {
                    assertEquals(5, mesa.getNumber());
                    assertEquals(4, mesa.getCapacity());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenMesaNotFound() {
        when(mesaRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(mesaService.getMesa("missing"))
                .expectError(MesaNotFoundException.class)
                .verify();
    }

    @Test
    void shouldListAllMesasWithoutFilter() {
        when(mesaRepository.findAll(Optional.empty())).thenReturn(Flux.just(sampleMesa));

        StepVerifier.create(mesaService.listMesas(null))
                .assertNext(mesa -> assertEquals(5, mesa.getNumber()))
                .verifyComplete();
    }

    @Test
    void shouldListMesasByStatus() {
        when(mesaRepository.findAll(Optional.of(MesaStatus.OCCUPIED)))
                .thenReturn(Flux.just(sampleMesa));

        StepVerifier.create(mesaService.listMesas("OCCUPIED"))
                .expectNextCount(1)
                .verifyComplete();

        verify(mesaRepository).findAll(Optional.of(MesaStatus.OCCUPIED));
    }

    @Test
    void shouldListMesasByStatusCaseInsensitive() {
        when(mesaRepository.findAll(Optional.of(MesaStatus.RESERVED)))
                .thenReturn(Flux.just(sampleMesa));

        StepVerifier.create(mesaService.listMesas("reserved"))
                .expectNextCount(1)
                .verifyComplete();

        verify(mesaRepository).findAll(Optional.of(MesaStatus.RESERVED));
    }

    @Test
    void shouldThrowWhenListingMesasWithInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> mesaService.listMesas("BOGUS"));
    }

    @Test
    void shouldUpdateMesaStatusToAvailable() {
        Mesa mesa = Mesa.builder().id("mesa-1").number(5).capacity(4)
                .status(MesaStatus.OCCUPIED).build();
        when(mesaRepository.findById("mesa-1")).thenReturn(Mono.just(mesa));
        when(mesaRepository.save(mesa)).thenReturn(Mono.just(mesa));

        StepVerifier.create(mesaService.updateMesaStatus("mesa-1", "AVAILABLE"))
                .assertNext(updated -> assertEquals(MesaStatus.AVAILABLE, updated.getStatus()))
                .verifyComplete();
    }

    @Test
    void shouldUpdateMesaStatusToOccupied() {
        Mesa mesa = Mesa.builder().id("mesa-1").number(5).capacity(4)
                .status(MesaStatus.AVAILABLE).build();
        when(mesaRepository.findById("mesa-1")).thenReturn(Mono.just(mesa));
        when(mesaRepository.save(mesa)).thenReturn(Mono.just(mesa));

        StepVerifier.create(mesaService.updateMesaStatus("mesa-1", "OCCUPIED"))
                .assertNext(updated -> assertEquals(MesaStatus.OCCUPIED, updated.getStatus()))
                .verifyComplete();
    }

    @Test
    void shouldUpdateMesaStatusToReserved() {
        Mesa mesa = Mesa.builder().id("mesa-1").number(5).capacity(4)
                .status(MesaStatus.AVAILABLE).build();
        when(mesaRepository.findById("mesa-1")).thenReturn(Mono.just(mesa));
        when(mesaRepository.save(mesa)).thenReturn(Mono.just(mesa));

        StepVerifier.create(mesaService.updateMesaStatus("mesa-1", "RESERVED"))
                .assertNext(updated -> assertEquals(MesaStatus.RESERVED, updated.getStatus()))
                .verifyComplete();
    }

    @Test
    void shouldUpdateMesaStatusToCleaning() {
        Mesa mesa = Mesa.builder().id("mesa-1").number(5).capacity(4)
                .status(MesaStatus.OCCUPIED).build();
        when(mesaRepository.findById("mesa-1")).thenReturn(Mono.just(mesa));
        when(mesaRepository.save(mesa)).thenReturn(Mono.just(mesa));

        StepVerifier.create(mesaService.updateMesaStatus("mesa-1", "CLEANING"))
                .assertNext(updated -> assertEquals(MesaStatus.CLEANING, updated.getStatus()))
                .verifyComplete();
    }

    @Test
    void shouldUpdateMesaStatusCaseInsensitive() {
        Mesa mesa = Mesa.builder().id("mesa-1").number(5).capacity(4)
                .status(MesaStatus.AVAILABLE).build();
        when(mesaRepository.findById("mesa-1")).thenReturn(Mono.just(mesa));
        when(mesaRepository.save(mesa)).thenReturn(Mono.just(mesa));

        StepVerifier.create(mesaService.updateMesaStatus("mesa-1", "occupied"))
                .assertNext(updated -> assertEquals(MesaStatus.OCCUPIED, updated.getStatus()))
                .verifyComplete();
    }

    @Test
    void shouldRejectInvalidStatusWhenUpdatingMesa() {
        StepVerifier.create(mesaService.updateMesaStatus("mesa-1", "BOGUS"))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(mesaRepository);
    }

    @Test
    void shouldFailWhenUpdatingNonExistentMesa() {
        when(mesaRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(mesaService.updateMesaStatus("missing", "OCCUPIED"))
                .expectError(MesaNotFoundException.class)
                .verify();

        verify(mesaRepository, never()).save(any(Mesa.class));
    }

    @Test
    void shouldFailWhenOccupyingMesaInCleaning() {
        Mesa mesa = Mesa.builder().id("mesa-1").number(5).capacity(4)
                .status(MesaStatus.CLEANING).build();
        when(mesaRepository.findById("mesa-1")).thenReturn(Mono.just(mesa));

        StepVerifier.create(mesaService.updateMesaStatus("mesa-1", "OCCUPIED"))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void shouldDeleteMesa() {
        when(mesaRepository.findById("mesa-1")).thenReturn(Mono.just(sampleMesa));
        when(mesaRepository.deleteById("mesa-1")).thenReturn(Mono.empty());

        StepVerifier.create(mesaService.deleteMesa("mesa-1"))
                .verifyComplete();

        verify(mesaRepository).deleteById("mesa-1");
    }

    @Test
    void shouldFailWhenDeletingNonExistentMesa() {
        when(mesaRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(mesaService.deleteMesa("missing"))
                .expectError(MesaNotFoundException.class)
                .verify();

        verify(mesaRepository, never()).deleteById(any());
    }

    @Test
    void shouldListAvailableMesas() {
        when(mesaRepository.findAvailable()).thenReturn(Flux.just(sampleMesa));

        StepVerifier.create(mesaService.listAvailableMesas())
                .assertNext(mesa -> assertEquals(MesaStatus.AVAILABLE, mesa.getStatus()))
                .verifyComplete();
    }
}
