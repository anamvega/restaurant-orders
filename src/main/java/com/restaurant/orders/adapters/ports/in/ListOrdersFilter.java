package com.restaurant.orders.adapters.ports.in;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Filter object for listing orders.
 */
public record ListOrdersFilter(
        Integer tableNumber,
        Optional<String> status,
        LocalDateTime dateFrom,
        LocalDateTime dateTo
) {
    public boolean hasFilters() {
        return tableNumber != null || status.isPresent() || dateFrom != null || dateTo != null;
    }
}
