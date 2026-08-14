package com.restaurant.orders.adapters.drivers.in.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Router configuration for mesa (table) HTTP endpoints.
 * Maps URLs to the functional endpoint handlers.
 */
@Configuration
public class MesaRouter {

    @Bean
    public RouterFunction<ServerResponse> mesaRoutes(MesaEndpoints mesaEndpoints) {
        return RouterFunctions.route()
                .path("/api", builder -> builder
                        .route(POST("/mesas").and(accept(MediaType.APPLICATION_JSON)),
                                mesaEndpoints::createMesa)
                        .route(GET("/mesas/{id}").and(accept(MediaType.APPLICATION_JSON)),
                                mesaEndpoints::getMesa)
                        .route(GET("/mesas").and(accept(MediaType.APPLICATION_JSON)),
                                mesaEndpoints::listMesas)
                        .route(GET("/mesas/available").and(accept(MediaType.APPLICATION_JSON)),
                                mesaEndpoints::listAvailableMesas)
                        .route(PATCH("/mesas/{id}/status").and(accept(MediaType.APPLICATION_JSON)),
                                mesaEndpoints::updateStatus)
                        .route(DELETE("/mesas/{id}"),
                                mesaEndpoints::deleteMesa)
                        .build())
                .build();
    }
}
