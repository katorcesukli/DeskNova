package com.capstone.desk_nova.dto.error;

import java.time.LocalDateTime;

public record ErrorResponse<E>(
        LocalDateTime timestamp,
        Integer status,
        String error,
        E message,
        String path
) {}
