package com.example.SmartLogi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryMovementRequestDTO (
    @NotNull(message = "inventoryId is required")
    Long inventoryId,

    @Min(value = 1, message = "quantity must be greater than 0")
    int quantity
            ){
}
