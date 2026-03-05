package com.capstone.desk_nova.dto.ticket;

import java.util.Map;

public record TicketMetric(
        long totalTickets,
        double avgResolutionTimeMinutes,
        double completionRate,
        Map<String, Long> ticketsByStatus,
        Map<String, Double> agentPerformance,
        Map<String, Map<String, Double>> avgResolvePerPriorityPerMonth
) { }
