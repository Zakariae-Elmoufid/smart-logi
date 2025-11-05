package com.example.SmartLogi.dto;

import jakarta.validation.constraints.NotBlank;

public record SupplierRequestDTO(
    @NotBlank
    String supplierName,
    @NotBlank String phoneNumber
){
}
