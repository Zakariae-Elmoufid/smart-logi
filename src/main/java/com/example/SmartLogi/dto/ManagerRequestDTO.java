package com.example.SmartLogi.dto;

import com.example.SmartLogi.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;



public record ManagerRequestDTO (
        @NotBlank(message = "First name is required")
                String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @UniqueEmail
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "\\+?\\d{10,15}", message = "Phone number must be numeric and 10-15 digits")
        String phoneNumber

) {}


