package com.example.SmartLogi.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "name is required")
        String name, String description,boolean active) {
}
