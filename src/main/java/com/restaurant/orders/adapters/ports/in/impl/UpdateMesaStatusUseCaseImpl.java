package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.UpdateMesaStatusUseCase;
import com.restaurant.orders.adapters.ports.out.MesaRepositoryPort;
import com.restaurant.orders.domain.model.Mesa;
import com.restaurant.orders.domain.model.MesaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the UpdateMesaStatusUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateMesaStatusUseCaseImpl implements UpdateMesaStatusUseCase {

    private final MesaRepositoryPort mesaRepository;

    @Override
    public Mono<Mesa> execute(String mesaId, String status) {
        log.info("Executing UpdateMesaStatusUseCase for mesa {} to status {}", mesaId, status);

        MesaStatus newStatus;
        try {
            newStatus = MesaStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Invalid status: " + status));
        }

        return mesaRepository.findById(mesaId)
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
}
