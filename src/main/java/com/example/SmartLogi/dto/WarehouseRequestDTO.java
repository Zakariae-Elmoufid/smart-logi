package com.example.SmartLogi.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record WarehouseRequestDTO (
        @NotBlank(message = "Warehouse name is required")
        String name,
        @NotBlank(message = "Address is required")
        String address,
        @NotBlank(message = "Warehouse code is required")
        String code,
        boolean active
) {
}
