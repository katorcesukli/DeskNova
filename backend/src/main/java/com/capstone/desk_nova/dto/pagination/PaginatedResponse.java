package com.capstone.desk_nova.dto.pagination;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> content,
        int currentPage,
        int totalPages,
        long totalItems,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious
) {}
