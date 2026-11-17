package com.restaurant.orders.domain.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationUtilsTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 10, Integer.MAX_VALUE})
    void shouldAcceptPositiveValues(Integer value) {
        assertDoesNotThrow(() -> ValidationUtils.requirePositive(value, "Capacity"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void shouldRejectNullZeroAndNegativeValues(Integer value) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requirePositive(value, "Capacity"));

        assertEquals("Capacity must be greater than 0", ex.getMessage());
    }

    @Test
    void shouldUseFieldNameInMessage() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requirePositive(0, "Table number"));

        assertEquals("Table number must be greater than 0", ex.getMessage());
    }
}
