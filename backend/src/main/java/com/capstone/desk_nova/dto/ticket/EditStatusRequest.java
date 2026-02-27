package com.capstone.desk_nova.dto.ticket;

import jakarta.validation.constraints.NotBlank;

public record EditStatusRequest(
        @NotBlank(message = "Status ID is required")
        String status

) {
        public EditStatusRequest {
                status = status.trim();
        }
}

