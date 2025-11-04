package com.example.SmartLogi.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SalesOrderRequestDTO(
        @NotNull(message = "ClientId must be provided")
        Long clientId,

        @NotNull(message = "WarehouseId must be provided")
        Long warehouseId,

        @NotNull(message = "Lines must be provided")
        List<SalesOrderLineRequestDTO> lines
){
}
