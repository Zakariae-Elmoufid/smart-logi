package com.example.SmartLogi.dto;

import jakarta.validation.constraints.NotBlank;

public record CarrierRequestDTO(
        @NotBlank(message = "Name is required")
         String carrierName,
         String phoneNumber
        ) {
}
