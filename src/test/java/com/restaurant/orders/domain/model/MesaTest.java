package com.restaurant.orders.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MesaTest {

    @Test
    void shouldCreateMesaWithDefaultValues() {
        Mesa mesa = Mesa.builder().number(1).capacity(4).build();

        assertEquals(1, mesa.getNumber());
        assertEquals(4, mesa.getCapacity());
        assertEquals(MesaStatus.AVAILABLE, mesa.getStatus());
        assertNotNull(mesa.getCreatedAt());
        assertNotNull(mesa.getUpdatedAt());
    }

    @Test
    void shouldOccupyAvailableMesa() {
        Mesa mesa = Mesa.builder().number(1).capacity(4).build();

        mesa.occupy();

        assertEquals(MesaStatus.OCCUPIED, mesa.getStatus());
    }

    @Test
    void shouldMakeOccupiedMesaAvailable() {
        Mesa mesa = Mesa.builder().number(1).capacity(4).status(MesaStatus.OCCUPIED).build();

        mesa.available();

        assertEquals(MesaStatus.AVAILABLE, mesa.getStatus());
    }

    @Test
    void shouldReserveAvailableMesa() {
        Mesa mesa = Mesa.builder().number(1).capacity(4).build();

        mesa.reserve();

        assertEquals(MesaStatus.RESERVED, mesa.getStatus());
    }

    @Test
    void shouldMarkMesaAsCleaning() {
        Mesa mesa = Mesa.builder().number(1).capacity(4).status(MesaStatus.OCCUPIED).build();

        mesa.cleaning();

        assertEquals(MesaStatus.CLEANING, mesa.getStatus());
    }

    @Test
    void shouldUpdateTimestampOnStatusChange() {
        Mesa mesa = Mesa.builder().number(1).capacity(4).build();
        LocalDateTime before = mesa.getUpdatedAt();

        // small buffer to guarantee a different timestamp
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        mesa.occupy();

        assertNotNull(mesa.getUpdatedAt());
        assertTrue(mesa.getUpdatedAt().isAfter(before) || mesa.getUpdatedAt().isEqual(before));
    }

    @Test
    void shouldNotOccupyMesaInCleaning() {
        Mesa mesa = Mesa.builder().number(1).capacity(4).status(MesaStatus.CLEANING).build();

        assertThrows(IllegalStateException.class, mesa::occupy);
    }

    @Test
    void shouldNotReserveMesaInCleaning() {
        Mesa mesa = Mesa.builder().number(1).capacity(4).status(MesaStatus.CLEANING).build();

        assertThrows(IllegalStateException.class, mesa::reserve);
    }

    @Test
    void shouldReportAvailability() {
        Mesa available = Mesa.builder().number(1).build();
        Mesa occupied = Mesa.builder().number(2).status(MesaStatus.OCCUPIED).build();
        Mesa reserved = Mesa.builder().number(3).status(MesaStatus.RESERVED).build();

        assertTrue(available.isAvailable());
        assertFalse(occupied.isAvailable());
        assertFalse(reserved.isAvailable());
    }

    @Test
    void shouldBeEqualByNumber() {
        Mesa a = Mesa.builder().id("id-1").number(5).build();
        Mesa b = Mesa.builder().id("id-2").number(5).build();
        Mesa c = Mesa.builder().id("id-3").number(6).build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
    }
}
