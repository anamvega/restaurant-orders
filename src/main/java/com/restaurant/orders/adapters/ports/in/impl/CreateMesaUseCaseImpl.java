package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.CreateMesaUseCase;
import com.restaurant.orders.adapters.ports.out.MesaRepositoryPort;
import com.restaurant.orders.domain.model.Mesa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the CreateMesaUseCase.
 * Validates input and delegates to the repository for persistence.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateMesaUseCaseImpl implements CreateMesaUseCase {

    private final MesaRepositoryPort mesaRepository;

    @Override
    public Mono<Mesa> execute(Integer number, Integer capacity) {
        log.info("Executing CreateMesaUseCase for table number {}", number);

        if (number == null || number <= 0) {
            return Mono.error(new IllegalArgumentException("Table number must be greater than 0"));
        }

        if (capacity == null || capacity <= 0) {
            return Mono.error(new IllegalArgumentException("Capacity must be greater than 0"));
        }

        return mesaRepository.existsByNumber(number)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Table number " + number + " already exists"));
                    }
                    Mesa mesa = Mesa.builder()
                            .number(number)
                            .capacity(capacity)
                            .build();
                    return mesaRepository.save(mesa);
                });
    }
}
