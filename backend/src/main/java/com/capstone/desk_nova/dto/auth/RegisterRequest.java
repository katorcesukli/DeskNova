package com.capstone.desk_nova.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must be between 1 & 100 characters long")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must be between 1 & 100 characters long")
        String lastName,

        @NotBlank(message = "Password is required")
        @Size(min = 4, message = "Password must be at least 4 characters")
        String password,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be in the correct format")
        String email
) {}
