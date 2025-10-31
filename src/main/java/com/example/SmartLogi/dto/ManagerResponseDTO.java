package com.example.SmartLogi.dto;

import java.time.LocalDateTime;

public record ManagerResponseDTO(
        Long id, String firstName, String lastName, String email, LocalDateTime createdAt, String role, boolean active

) {
}
