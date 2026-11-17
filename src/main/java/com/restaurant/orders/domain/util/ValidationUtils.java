package com.restaurant.orders.domain.util;


public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requirePositive(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0");
        }
    }
}
