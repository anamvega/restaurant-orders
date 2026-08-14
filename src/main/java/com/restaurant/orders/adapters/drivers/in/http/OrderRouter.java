package com.restaurant.orders.adapters.drivers.in.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Router configuration for order HTTP endpoints.
 * Maps URLs to the functional endpoint handlers.
 */
@Configuration
public class OrderRouter {

    @Bean
    public RouterFunction<ServerResponse> orderRoutes(OrderEndpoints orderEndpoints) {
        return RouterFunctions.route()
                .path("/api", builder -> builder
                        .route(GET("/orders").and(accept(MediaType.APPLICATION_JSON)),
                                orderEndpoints::listOrders)
                        .route(POST("/orders").and(accept(MediaType.APPLICATION_JSON)),
                                orderEndpoints::createOrder)
                        .route(GET("/orders/{id}").and(accept(MediaType.APPLICATION_JSON)),
                                orderEndpoints::getOrder)
                        .route(PATCH("/orders/{id}/status").and(accept(MediaType.APPLICATION_JSON)),
                                orderEndpoints::updateStatus)
                        .route(DELETE("/orders/{id}"),
                                orderEndpoints::deleteOrder)
                        .route(POST("/orders/{id}/items").and(accept(MediaType.APPLICATION_JSON)),
                                orderEndpoints::addOrderItem)
                        .route(DELETE("/orders/{id}/items/{dishName}"),
                                orderEndpoints::removeOrderItem)
                        .build())
                .build();
    }
}
