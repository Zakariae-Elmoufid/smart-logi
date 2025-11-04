package com.example.SmartLogi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SalesOrderLineRequestDTO(
        @NotNull(message = "ProductId must be provided")
        Long productId,

        @Positive(message = "Quantity must be > 0")
        int quantityRequested


) {
}
