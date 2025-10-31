package com.example.SmartLogi.dto;

import com.example.SmartLogi.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ManagerUpdateDTO(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @UniqueEmail
        String email,

        boolean active,
        Set<Long> warehouseIds
) {
}
