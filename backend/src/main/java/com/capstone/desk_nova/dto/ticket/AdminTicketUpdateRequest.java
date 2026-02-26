package com.capstone.desk_nova.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminTicketUpdateRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Category is required")
        String category,

        @NotBlank(message = "Status is required")
        String status,

        @NotBlank(message = "Priority is required")
        String priority,

        Long agentId
) {}