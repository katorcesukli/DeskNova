package com.capstone.desk_nova.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketCommentRequest(
        @NotNull(message = "Id is required")
        Long id,

        @NotBlank(message = "Ticket comment is required")
        String comment
) {}
