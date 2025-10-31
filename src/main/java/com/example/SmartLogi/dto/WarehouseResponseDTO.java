package com.example.SmartLogi.dto;

public record WarehouseResponseDTO(
        long id,
        String name,
        String code,
        String address,
        boolean active
) {

}
