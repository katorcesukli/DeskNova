package com.capstone.desk_nova.dto.ticket;

import com.capstone.desk_nova.dto.person.TicketPersonResponse;
import com.capstone.desk_nova.model.Tickets;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record TicketResponse(
        Long id,
        String title,
        String description,
        String category,
        String status,
        String priority,
        TicketPersonResponse client,
        @Nullable
        TicketPersonResponse agent,
        List<TicketCommentResponse> comments,
        LocalDateTime dateOpened,
        LocalDateTime dateClosed,
        LocalDateTime assignedAt,
        LocalDateTime updatedAt
) {

        public static TicketResponse from(Tickets t) {
                return new TicketResponse(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getCategory().name(),
                        t.getStatus().name(),
                        t.getPriority().getName(),
                        TicketPersonResponse.from(t.getClient()),
                        Optional.ofNullable(t.getAgent()).map(TicketPersonResponse::from).orElse(null),
                        t.getComments().stream().map(c -> new TicketCommentResponse(
                                c.getId(),
                                TicketPersonResponse.from(c.getUserId()),
                                c.getComment(),
                                c.getCreatedAt()
                        )).toList(),
                        t.getDateOpened(),
                        t.getDateClosed(),
                        t.getAssignedAt(),
                        t.getUpdatedAt()
                );
        }
}
