package com.capstone.desk_nova.dto.ticket;

import com.capstone.desk_nova.dto.person.PersonResponse;

import java.time.LocalDateTime;

public record TicketCommentResponse(
    Long id,
    PersonResponse user,
    String comment,
    LocalDateTime createdAt
) {}
