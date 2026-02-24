package com.capstone.desk_nova.dto.auth;

public record AuthResponse(
    String token,
    Long id,
    String firstName,
    String lastName,
    String email,
    String role
) {}
