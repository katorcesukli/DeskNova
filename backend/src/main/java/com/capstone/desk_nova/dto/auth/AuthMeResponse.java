package com.capstone.desk_nova.dto.auth;

public record AuthMeResponse(
        String email,
        String role,
        boolean isAuthenticated
) {}
