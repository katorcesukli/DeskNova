package com.capstone.desk_nova.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketCommentRequest(
        @NotNull(message = "Ticket id is required")
        Long ticketId,

        @NotBlank(message = "Ticket comment is required")
        String comment
) {}
