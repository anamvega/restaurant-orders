package com.restaurant.orders.domain.service;

import com.restaurant.orders.adapters.ports.out.MesaRepositoryPort;
import com.restaurant.orders.domain.model.Mesa;
import com.restaurant.orders.domain.model.MesaStatus;
import com.restaurant.orders.infrastructure.exceptions.MesaAlreadyExistsException;
import com.restaurant.orders.infrastructure.exceptions.MesaNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.restaurant.orders.domain.util.ValidationUtils.requirePositive;

/**
 * Application Service that orchestrates all use cases for mesa (table) management.
 * This service acts as the facade between the HTTP adapters and the domain layer.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MesaApplicationService {

    private final MesaRepositoryPort mesaRepository;

    /**
     * Create a new restaurant table.
     */
    @Transactional
    public Mono<Mesa> createMesa(Integer number, Integer capacity) {
        log.info("Creating mesa with number {} and capacity {}", number, capacity);

           return Mono.defer(() -> {
                    requirePositive(number, "Table number");
                    requirePositive(capacity, "Capacity");
                    return mesaRepository.existsByNumber(number);
                })
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new MesaAlreadyExistsException(number));
                    }
                    return mesaRepository.save(Mesa.builder()
                            .number(number)
                            .capacity(capacity)
                            .build());
                });
    }

    /**
     * Get a mesa by its ID.
     */
    public Mono<Mesa> getMesa(String mesaId) {
        log.info("Fetching mesa with ID: {}", mesaId);
        return findExisting(mesaId);
    }

    /**
     * List all mesas with optional status filter.
     */
    public Flux<Mesa> listMesas(String status) {
        log.info("Listing mesas with status filter: {}", status);

        Optional<MesaStatus> statusOpt = Optional.empty();
        if (status != null && !status.isBlank()) {
            try {
                statusOpt = Optional.of(MesaStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }

        if (statusOpt.isPresent()) {
            return mesaRepository.findAll(statusOpt);
        }
        return mesaRepository.findAll(statusOpt);
    }

    /**
     * Update the status of a mesa.
     */
    @Transactional
    public Mono<Mesa> updateMesaStatus(String mesaId, String status) {
        log.info("Updating mesa {} status to {}", mesaId, status);

        MesaStatus newStatus;
        try {
            newStatus = MesaStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Invalid status: " + status));
        }

        return findExisting(mesaId)
                .flatMap(mesa -> {
                    switch (newStatus) {
                        case AVAILABLE -> mesa.available();
                        case OCCUPIED -> mesa.occupy();
                        case RESERVED -> mesa.reserve();
                        case CLEANING -> mesa.cleaning();
                        default -> throw new IllegalArgumentException("Invalid status: " + status);
                    }
                    return mesaRepository.save(mesa);
                });
    }

    /**
     * Delete a mesa by its ID.
     */
    @Transactional
    public Mono<Void> deleteMesa(String mesaId) {
        log.info("Deleting mesa with ID: {}", mesaId);
        return findExisting(mesaId)
                .flatMap(mesa -> mesaRepository.deleteById(mesaId));
    }

    /**
     * List all available mesas.
     */
    public Flux<Mesa> listAvailableMesas() {
        log.info("Listing available mesas");
        return mesaRepository.findAvailable();
    }

    private Mono<Mesa> findExisting(String mesaId) {
        return mesaRepository.findById(mesaId)
                .switchIfEmpty(Mono.error(new MesaNotFoundException(mesaId)));
    }
}
