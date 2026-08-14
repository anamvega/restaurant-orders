package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.DeleteMesaUseCase;
import com.restaurant.orders.adapters.ports.out.MesaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the DeleteMesaUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteMesaUseCaseImpl implements DeleteMesaUseCase {

    private final MesaRepositoryPort mesaRepository;

    @Override
    public Mono<Void> execute(String mesaId) {
        log.info("Executing DeleteMesaUseCase for mesa ID: {}", mesaId);
        return mesaRepository.deleteById(mesaId);
    }
}
