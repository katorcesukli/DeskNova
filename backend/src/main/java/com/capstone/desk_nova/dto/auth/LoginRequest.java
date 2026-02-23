package com.capstone.desk_nova.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be in the correct format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 4, message = "Password must be at least 4 characters")
        String password
) {}
