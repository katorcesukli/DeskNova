package com.capstone.desk_nova.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
        @NotBlank(message = "Ticket title is required")
        @Size(max = 255, message = "Ticket title must be between 1 & 255 characters long")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Status is required")
        String status,

        @NotBlank(message = "Priority is required")
        String priority,

        @NotBlank(message = "Ticket category is required")
        String category
) {}
