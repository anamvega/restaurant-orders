package com.restaurant.orders.domain.util;


public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requirePositive(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(ErrorMessages.format(ErrorMessages.MUST_BE_POSITIVE, fieldName));
        }
    }
}
