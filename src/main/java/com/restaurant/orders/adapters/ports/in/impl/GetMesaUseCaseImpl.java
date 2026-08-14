package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.GetMesaUseCase;
import com.restaurant.orders.adapters.ports.out.MesaRepositoryPort;
import com.restaurant.orders.domain.model.Mesa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the GetMesaUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetMesaUseCaseImpl implements GetMesaUseCase {

    private final MesaRepositoryPort mesaRepository;

    @Override
    public Mono<Mesa> execute(String mesaId) {
        log.info("Executing GetMesaUseCase for mesa ID: {}", mesaId);
        return mesaRepository.findById(mesaId);
    }
}
