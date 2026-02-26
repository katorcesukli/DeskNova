package com.capstone.desk_nova.dto.person;

import com.capstone.desk_nova.model.Users;

public record TicketPersonResponse(
        String fullName,
        String email
) {

    public static TicketPersonResponse from(Users user){
        return new TicketPersonResponse(user.getFullName(),  user.getEmail());
    }
}
