package com.restaurant.orders.infrastructure.exceptions;

import com.restaurant.orders.adapters.ports.in.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    private MockServerWebExchange exchangeFor(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }

    @Test
    void shouldReturn404WhenOrderNotFound() {
        OrderNotFoundException ex = new OrderNotFoundException("missing");

        ResponseEntity<ErrorResponse> response = handler.handleOrderNotFound(ex, exchangeFor("/api/orders/missing")).block();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Order not found with ID: missing", response.getBody().getMessage());
        assertEquals("/api/orders/missing", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldReturn404WhenMesaNotFound() {
        MesaNotFoundException ex = new MesaNotFoundException("missing");

        ResponseEntity<ErrorResponse> response = handler.handleMesaNotFound(ex, exchangeFor("/api/mesas/missing")).block();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Mesa not found with ID: missing", response.getBody().getMessage());
        assertEquals("/api/mesas/missing", response.getBody().getPath());
    }

    @Test
    void shouldReturn409WhenMesaAlreadyExists() {
        MesaAlreadyExistsException ex = new MesaAlreadyExistsException(5);

        ResponseEntity<ErrorResponse> response = handler.handleMesaAlreadyExists(ex, exchangeFor("/api/mesas")).block();

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("Mesa number 5 already exists", response.getBody().getMessage());
    }

    @Test
    void shouldReturn400ForInvalidOrder() {
        InvalidOrderException ex = new InvalidOrderException("Invalid operation");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidOrder(ex, exchangeFor("/api/orders/1")).block();

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid operation", response.getBody().getMessage());
    }

    @Test
    void shouldReturn409ForIllegalState() {
        IllegalStateException ex = new IllegalStateException("Cannot modify order in terminal state: CANCELLED");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalState(ex, exchangeFor("/api/orders/1/items")).block();

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("Cannot modify order in terminal state: CANCELLED", response.getBody().getMessage());
    }

    @Test
    void shouldReturn400ForIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Table number must be greater than 0");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, exchangeFor("/api/orders")).block();

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Table number must be greater than 0", response.getBody().getMessage());
    }

    @Test
    void shouldReturn500ForUnexpectedError() {
        RuntimeException ex = new RuntimeException("boom");

        ResponseEntity<ErrorResponse> response = handler.handleGeneral(ex, exchangeFor("/api/orders")).block();

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}
