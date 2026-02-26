package com.capstone.desk_nova.dto.ticket;

import com.capstone.desk_nova.dto.person.TicketPersonResponse;

import java.time.LocalDateTime;

public record TicketCommentResponse(
    Long id,
    TicketPersonResponse user,
    String comment,
    LocalDateTime createdAt
) {}
