package com.restaurant.orders.adapters.drivers.in.http;

import com.restaurant.orders.domain.model.Mesa;
import com.restaurant.orders.domain.service.MesaApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MesaEndpointsTest {

    @Mock
    private MesaApplicationService mesaService;

    private WebTestClient client;

    private Mesa sampleMesa;

    @BeforeEach
    void setUp() {
        sampleMesa = Mesa.builder().id("mesa-1").number(5).capacity(4).build();

        MesaEndpoints endpoints = new MesaEndpoints(mesaService);
        MesaRouter router = new MesaRouter();
        RouterFunction<ServerResponse> route = router.mesaRoutes(endpoints);

        client = WebTestClient.bindToRouterFunction(route).build();
    }

    @Test
    void shouldCreateMesa() {
        when(mesaService.createMesa(5, 4)).thenReturn(Mono.just(sampleMesa));

        client.post().uri("/api/mesas")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new MesaCreateRequest(5, 4))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.number").isEqualTo(5)
                .jsonPath("$.capacity").isEqualTo(4)
                .jsonPath("$.status").isEqualTo("AVAILABLE");
    }

    @Test
    void shouldGetMesaById() {
        when(mesaService.getMesa("mesa-1")).thenReturn(Mono.just(sampleMesa));

        client.get().uri("/api/mesas/mesa-1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("mesa-1")
                .jsonPath("$.number").isEqualTo(5);
    }

    @Test
    void shouldListMesasWithStatusFilter() {
        when(mesaService.listMesas("OCCUPIED")).thenReturn(Flux.just(sampleMesa));

        client.get().uri("/api/mesas?status=OCCUPIED")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].number").isEqualTo(5);
    }

    @Test
    void shouldListMesasWithoutFilter() {
        when(mesaService.listMesas(null)).thenReturn(Flux.just(sampleMesa));

        client.get().uri("/api/mesas")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].number").isEqualTo(5);
    }

    @Test
    void shouldListAvailableMesas() {
        when(mesaService.listAvailableMesas()).thenReturn(Flux.just(sampleMesa));

        client.get().uri("/api/mesas/available")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].status").isEqualTo("AVAILABLE");
    }

    @Test
    void shouldUpdateMesaStatus() {
        when(mesaService.updateMesaStatus(eq("mesa-1"), eq("OCCUPIED")))
                .thenReturn(Mono.just(sampleMesa));

        client.patch().uri("/api/mesas/mesa-1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new MesaStatusRequest("OCCUPIED"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.number").isEqualTo(5);
    }

    @Test
    void shouldDeleteMesa() {
        when(mesaService.deleteMesa("mesa-1")).thenReturn(Mono.empty());

        client.delete().uri("/api/mesas/mesa-1")
                .exchange()
                .expectStatus().isNoContent();

        verify(mesaService).deleteMesa("mesa-1");
    }
}
