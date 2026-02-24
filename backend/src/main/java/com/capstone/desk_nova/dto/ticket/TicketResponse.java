package com.capstone.desk_nova.dto.ticket;

import com.capstone.desk_nova.dto.person.PersonResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;
import java.util.List;

public record TicketResponse(
        Long id,
        String title,
        String description,
        String category,
        String status,
        String priority,
        PersonResponse client,
        @Nullable
        PersonResponse agent,
        List<TicketCommentResponse> comments,
        LocalDateTime dateOpened,
        LocalDateTime dateClosed,
        LocalDateTime assignedAt,
        LocalDateTime updatedAt
) {}
