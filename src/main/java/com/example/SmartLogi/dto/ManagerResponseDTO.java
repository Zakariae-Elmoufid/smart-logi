package com.example.SmartLogi.dto;

import java.time.LocalDateTime;

public record ManagerResponseDTO(
        Long id, String firstName, String lastName, String email, LocalDateTime createdAt, String phoneNumber, String role, boolean active

) {
}
