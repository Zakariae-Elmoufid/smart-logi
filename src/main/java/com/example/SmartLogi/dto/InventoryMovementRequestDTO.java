package com.example.SmartLogi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryMovementRequestDTO (
    @NotNull(message = "inventoryId is required")
    Long inventoryId,

    @NotNull(message = "quantity is required")
    int quantity
            ){
}
