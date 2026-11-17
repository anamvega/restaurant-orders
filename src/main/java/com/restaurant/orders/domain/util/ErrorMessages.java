package com.restaurant.orders.domain.util;

public final class ErrorMessages {

    private ErrorMessages() {
    }

    // ==================== Field names ====================

    public static final String FIELD_TABLE_NUMBER = "Table number";
    public static final String FIELD_CAPACITY = "Capacity";
    public static final String FIELD_QUANTITY = "Quantity";
    public static final String FIELD_UNIT_PRICE = "Unit price";

    // ==================== Validation ====================

    public static final String MUST_BE_POSITIVE = "%s must be greater than 0";
    public static final String INVALID_STATUS = "Invalid status: %s";
    public static final String INVALID_DATE_FORMAT = "Invalid date format for %s. Use ISO format: yyyy-MM-ddTHH:mm:ss";
    public static final String VALIDATION_FAILED = "Validation failed: %s";

    // ==================== Orders ====================

    public static final String ORDER_NOT_FOUND = "Order not found with ID: %s";
    public static final String ORDER_WITHOUT_ITEMS = "Order must have at least one item";
    public static final String ORDER_INVALID_TRANSITION = "Cannot transition from %s to %s";
    public static final String ORDER_TERMINAL_STATE = "Cannot modify order in terminal state: %s";
    public static final String ORDER_ITEM_NOT_FOUND = "Item not found: %s";

    // ==================== Mesas ====================

    public static final String MESA_NOT_FOUND = "Mesa not found with ID: %s";
    public static final String MESA_ALREADY_EXISTS = "Mesa number %s already exists";
    public static final String MESA_BEING_CLEANED = "Table is being cleaned and cannot be modified";

    // ==================== Generic ====================

    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";

    public static String format(String template, Object... args) {
        return String.format(template, args);
    }
}
