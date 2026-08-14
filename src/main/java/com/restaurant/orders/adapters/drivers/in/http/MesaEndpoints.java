package com.restaurant.orders.adapters.drivers.in.http;

import com.restaurant.orders.domain.model.Mesa;
import com.restaurant.orders.domain.service.MesaApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * WebFlux Functional Endpoint for mesa (table) management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MesaEndpoints {

    private final MesaApplicationService mesaService;

    /**
     * POST /api/mesas - Create a new mesa.
     */
    public Mono<ServerResponse> createMesa(ServerRequest request) {
        Mono<MesaCreateRequest> body = request.bodyToMono(MesaCreateRequest.class);

        return body
                .doOnNext(req -> log.info("Received create mesa request for table {}", req.number()))
                .flatMap(req -> mesaService.createMesa(req.number(), req.capacity()))
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED).bodyValue(response));
    }

    /**
     * GET /api/mesas/{id} - Get mesa by ID.
     */
    public Mono<ServerResponse> getMesa(ServerRequest request) {
        String mesaId = request.pathVariable("id");

        return mesaService.getMesa(mesaId)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    /**
     * GET /api/mesas - List all mesas with optional status filter.
     */
    public Mono<ServerResponse> listMesas(ServerRequest request) {
        String status = request.queryParam("status").orElse(null);

        Flux<Mesa> response = mesaService.listMesas(status);

        return ServerResponse.ok().body(response, Mesa.class);
    }

    /**
     * GET /api/mesas/available - List all available mesas.
     */
    public Mono<ServerResponse> listAvailableMesas(ServerRequest request) {
        Flux<Mesa> response = mesaService.listAvailableMesas();

        return ServerResponse.ok().body(response, Mesa.class);
    }

    /**
     * PATCH /api/mesas/{id}/status - Update mesa status.
     */
    public Mono<ServerResponse> updateStatus(ServerRequest request) {
        String mesaId = request.pathVariable("id");
        Mono<MesaStatusRequest> body = request.bodyToMono(MesaStatusRequest.class);

        return body
                .doOnNext(req -> log.info("Updating mesa {} status to {}", mesaId, req.status()))
                .map(MesaStatusRequest::status)
                .flatMap(newStatus -> mesaService.updateMesaStatus(mesaId, newStatus))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    /**
     * DELETE /api/mesas/{id} - Delete a mesa.
     */
    public Mono<ServerResponse> deleteMesa(ServerRequest request) {
        String mesaId = request.pathVariable("id");

        return mesaService.deleteMesa(mesaId)
                .then(ServerResponse.status(HttpStatus.NO_CONTENT).build());
    }
}
