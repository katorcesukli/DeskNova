package com.capstone.desk_nova.dto.auth;

import com.capstone.desk_nova.model.Users;

public record AuthResponse(
    String token,
    Long id,
    String firstName,
    String lastName,
    String email,
    String role
) {

    public static AuthResponse from(String token, Users user){
        return new AuthResponse(
                token,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
