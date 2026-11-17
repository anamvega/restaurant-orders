package com.restaurant.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.restaurant.orders.domain.util.ErrorMessages.MESA_BEING_CLEANED;

/**
 * Aggregate Root representing a restaurant table.
 * 
 * This entity encapsulates all business rules for table management:
 * - Status transitions are validated
 * - Timestamps are managed automatically
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "mesas")
public class Mesa {

    @Id
    private String id;

    private Integer number;

    private Integer capacity;

    @Builder.Default
    private MesaStatus status = MesaStatus.AVAILABLE;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Domain method: Mark the table as occupied.
     */
    public void occupy() {
        validateNotInTerminalState();
        this.status = MesaStatus.OCCUPIED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Domain method: Mark the table as available.
     */
    public void available() {
        this.status = MesaStatus.AVAILABLE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Domain method: Reserve the table.
     */
    public void reserve() {
        validateNotInTerminalState();
        this.status = MesaStatus.RESERVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Domain method: Mark the table as cleaning.
     */
    public void cleaning() {
        this.status = MesaStatus.CLEANING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Domain method: Validate status transitions.
     */
    private void validateNotInTerminalState() {
        if (this.status == MesaStatus.CLEANING) {
            throw new IllegalStateException(MESA_BEING_CLEANED);
        }
    }

    /**
     * Domain method: Check if the table is available.
     */
    public boolean isAvailable() {
        return this.status == MesaStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mesa mesa = (Mesa) o;
        return Objects.equals(number, mesa.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
