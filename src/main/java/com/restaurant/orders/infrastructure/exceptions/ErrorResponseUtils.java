package com.restaurant.orders.infrastructure.exceptions;

import com.restaurant.orders.adapters.ports.in.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class ErrorResponseUtils {

    private ErrorResponseUtils() {
    }

    public static ErrorResponse build(HttpStatus status, String message, ServerWebExchange exchange) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getPath().toString())
                .build();
    }

    public static Mono<ResponseEntity<ErrorResponse>> toResponse(HttpStatus status, String message,
                                                                 ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(status).body(build(status, message, exchange)));
    }
}
