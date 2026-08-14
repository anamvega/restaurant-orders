package com.restaurant.orders.adapters.ports.in.impl;

import com.restaurant.orders.adapters.ports.in.ListMesaUseCase;
import com.restaurant.orders.adapters.ports.out.MesaRepositoryPort;
import com.restaurant.orders.domain.model.Mesa;
import com.restaurant.orders.domain.model.MesaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;

/**
 * Implementation of the ListMesaUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListMesaUseCaseImpl implements ListMesaUseCase {

    private final MesaRepositoryPort mesaRepository;

    @Override
    public Flux<Mesa> execute(String status) {
        log.info("Executing ListMesaUseCase with status filter: {}", status);

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
}
