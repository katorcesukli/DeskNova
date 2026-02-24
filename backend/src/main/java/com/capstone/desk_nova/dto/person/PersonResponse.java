package com.capstone.desk_nova.dto.person;

import com.capstone.desk_nova.model.Users;

public record PersonResponse(
        String fullName,
        String email
) {

    public static PersonResponse from(Users user){
        return new PersonResponse(user.getFullName(),  user.getEmail());
    }
}
