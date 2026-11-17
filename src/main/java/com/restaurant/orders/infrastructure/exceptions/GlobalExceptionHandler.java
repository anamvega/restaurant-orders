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

import java.util.stream.Collectors;

import static com.restaurant.orders.domain.util.ErrorMessages.UNEXPECTED_ERROR;
import static com.restaurant.orders.domain.util.ErrorMessages.VALIDATION_FAILED;
import static com.restaurant.orders.domain.util.ErrorMessages.format;
import static com.restaurant.orders.infrastructure.exceptions.ErrorResponseUtils.toResponse;


@RestControllerAdvice
@Slf4j
@Order(-2)
public class GlobalExceptionHandler {


    @ExceptionHandler(OrderNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleOrderNotFound(OrderNotFoundException ex,
                                                                    ServerWebExchange exchange) {
        log.error("Order not found: {}", ex.getMessage());
        return toResponse(HttpStatus.NOT_FOUND, ex.getMessage(), exchange);
    }


    @ExceptionHandler(MesaNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMesaNotFound(MesaNotFoundException ex,
                                                                   ServerWebExchange exchange) {
        log.error("Mesa not found: {}", ex.getMessage());
        return toResponse(HttpStatus.NOT_FOUND, ex.getMessage(), exchange);
    }

    @ExceptionHandler(MesaAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMesaAlreadyExists(MesaAlreadyExistsException ex,
                                                                        ServerWebExchange exchange) {
        log.error("Mesa already exists: {}", ex.getMessage());
        return toResponse(HttpStatus.CONFLICT, ex.getMessage(), exchange);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationErrors(WebExchangeBindException ex,
                                                                       ServerWebExchange exchange) {
        log.error("Validation failed: {}", ex.getMessage());

        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return toResponse(HttpStatus.BAD_REQUEST, format(VALIDATION_FAILED, details), exchange);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidOrder(InvalidOrderException ex,
                                                                   ServerWebExchange exchange) {
        log.error("Invalid order operation: {}", ex.getMessage());
        return toResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange);
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalState(IllegalStateException ex,
                                                                   ServerWebExchange exchange) {
        log.error("Illegal state: {}", ex.getMessage());
        return toResponse(HttpStatus.CONFLICT, ex.getMessage(), exchange);
    }

    /**
     * Handle IllegalArgumentException - returns 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgument(IllegalArgumentException ex,
                                                                      ServerWebExchange exchange) {
        log.error("Illegal argument: {}", ex.getMessage());
        return toResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange);
    }

    /**
     * Catch-all handler for unexpected errors - returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneral(Exception ex,
                                                              ServerWebExchange exchange) {
        log.error("Unexpected error", ex);
        return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR, exchange);
    }
}
