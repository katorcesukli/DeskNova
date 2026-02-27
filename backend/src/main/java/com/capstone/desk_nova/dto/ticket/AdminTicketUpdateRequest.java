package com.capstone.desk_nova.dto.ticket;

import jakarta.validation.constraints.NotBlank;

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
) {
        public AdminTicketUpdateRequest {
                title = title.trim();
                description = description.trim();
                category = category.trim();
                status = status.trim();
                priority = priority.trim();
        }
}