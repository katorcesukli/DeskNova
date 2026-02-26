package com.capstone.desk_nova.dto.person;

import com.capstone.desk_nova.model.Users;

import java.time.LocalDateTime;

public record UserResponse (
    Long id,
    String firstName,
    String lastName,
    String role,
    String email,
    String password,
    LocalDateTime createdAt
) {
    public static UserResponse from(Users user){
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getEmail(),
                user.getPassword(),
                user.getCreatedAt()
        );
    }
}
