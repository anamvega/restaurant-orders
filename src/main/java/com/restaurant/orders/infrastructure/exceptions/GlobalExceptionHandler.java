package com.restaurant.orders.infrastructure.exceptions;

import com.restaurant.orders.adapters.ports.in.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

/**
 * Global error handler for all WebFlux controllers.
 * Provides consistent error responses across the API.
 */
@RestControllerAdvice
@Slf4j
@Order(-2)
public class GlobalExceptionHandler {

    /**
     * Handle OrderNotFoundException - returns 404 Not Found.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleOrderNotFound(OrderNotFoundException ex,
                                                                    ServerWebExchange exchange) {
        log.error("Order not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).toString())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().toString())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    /**
     * Handle validation errors from @Valid annotations - returns 400 Bad Request.
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationErrors(WebExchangeBindException ex,
                                                                       ServerWebExchange exchange) {
        log.error("Validation failed: {}", ex.getMessage());

        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validation failed: " + details)
                .path(exchange.getRequest().getPath().toString())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    /**
     * Handle InvalidOrderException - returns 400 Bad Request.
     */
    @ExceptionHandler(InvalidOrderException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidOrder(InvalidOrderException ex,
                                                                   ServerWebExchange exchange) {
        log.error("Invalid order operation: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().toString())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    /**
     * Handle IllegalStateException (e.g., terminal state modifications) - returns 409 Conflict.
     */
    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalState(IllegalStateException ex,
                                                                   ServerWebExchange exchange) {
        log.error("Illegal state: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).toString())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().toString())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
    }

    /**
     * Handle IllegalArgumentException - returns 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgument(IllegalArgumentException ex,
                                                                      ServerWebExchange exchange) {
        log.error("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().toString())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    /**
     * Catch-all handler for unexpected errors - returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneral(Exception ex,
                                                              ServerWebExchange exchange) {
        log.error("Unexpected error", ex);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).toString())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(exchange.getRequest().getPath().toString())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}
