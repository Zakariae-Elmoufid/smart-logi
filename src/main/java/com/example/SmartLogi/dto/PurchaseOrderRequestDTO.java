package com.example.SmartLogi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderRequestDTO(
        @Positive(message = "Supplier ID must be positive")
        long supplierId,

        @Positive(message = "Warehouse ID must be positive")
        long warehouseId,

        @NotNull(message = "Expected date is required")
        @FutureOrPresent(message = "Expected date must be today or in the future")
        LocalDateTime expectedDate,

        @NotNull(message = "Purchase order lines are required")
        @Size(min = 1, message = "At least one purchase order line is required")
        List<@Valid PurchaseOrderLineRequestDTO> liens
) {
}
