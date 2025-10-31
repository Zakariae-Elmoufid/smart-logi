package com.example.SmartLogi.dto;

import jakarta.validation.constraints.*;

public record ProductRequestDTO(
        @NotBlank(message = "Product name is required")
        @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
        String name,
        @NotBlank(message = "SKU is required")
        @Pattern(regexp = "^[A-Z0-9_-]+$", message = "SKU must contain only uppercase letters, numbers, underscores or dashes")
        String sku,
        @PositiveOrZero(message = "Purchase price must be greater than or equal to 0")
        double purchasePrice,
        @Positive(message = "Selling price must be greater than 0")
        double sellingPrice ,boolean active,
        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long categoryId
) {
}
