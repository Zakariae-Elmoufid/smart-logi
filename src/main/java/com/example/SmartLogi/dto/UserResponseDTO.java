package com.example.SmartLogi.dto;

import com.example.SmartLogi.enums.UserRole;
import lombok.Builder;

@Builder
public record UserResponseDTO (Long id, String firstName, String lastName, String email ,UserRole role) {
}
