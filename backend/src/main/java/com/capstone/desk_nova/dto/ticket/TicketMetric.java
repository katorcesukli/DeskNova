package com.capstone.desk_nova.dto.ticket;

import com.capstone.desk_nova.dto.person.PersonResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record TicketMetric(
        long totalTickets,
        double avgResolutionTimeMinutes,
        double completionRate,
        Map<String, Long> ticketsByStatus,
        Map<String, Double> agentPerformance
) { }
