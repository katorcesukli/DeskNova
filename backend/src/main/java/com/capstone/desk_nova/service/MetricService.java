package com.capstone.desk_nova.service;

import com.capstone.desk_nova.dto.ticket.TicketMetric;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.enums.TicketStatus;
import com.capstone.desk_nova.repository.TicketsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final TicketsRepository ticketsRepository;

    public TicketMetric getAdminMetrics() {
        List<Tickets> allTickets = ticketsRepository.findAll();

        //total ticket
        long totalTickets = allTickets.size();

        //ave resolution time in minutes
        List<Tickets> closedTickets = allTickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.RESOLVED && t.getDateClosed() != null)
                .toList();

        double avgResolutionTime = closedTickets.stream()
                .mapToLong(t -> Duration.between(t.getDateOpened(), t.getDateClosed()).toMinutes())
                .average()
                .orElse(0.0);

        //percentage of completion
        double completionRate = totalTickets == 0 ? 0 : ((double) closedTickets.size() / totalTickets) * 100;

        //grouping tickets by status
        Map<String, Long> ticketsByStatus = allTickets.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()
                ));

        //per agent performance,
        Map<String, Double> agentPerformance = closedTickets.stream()
                .filter(t -> t.getAgent() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getAgent().getFullName(),
                        Collectors.averagingLong(t -> Duration.between(t.getDateOpened(), t.getDateClosed()).toMinutes())
                ));

        //getAveResolvePerPriority put here
        Map<String, Map<String, Double>> resolvePerPriority = getAveResolvePerPriority();

        return new TicketMetric(
                totalTickets,
                avgResolutionTime,
                completionRate,
                ticketsByStatus,
                agentPerformance,
                resolvePerPriority
        );
    }

    public List<Tickets> getAllTickets() {
        return ticketsRepository.findAll();
    }

    public Map<String, Map<String, Double>> getAveResolvePerPriority() {
        List<Tickets> allTickets = ticketsRepository.findAll();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        return allTickets.stream()
                //filter for resolved tickets with valid dates
                .filter(t -> t.getStatus() != null && t.getStatus().name().equalsIgnoreCase("RESOLVED"))
                .filter(t -> t.getDateOpened() != null && t.getDateClosed() != null)
                //filter for != priority
                .filter(t -> t.getPriority() != null)
                //filter by priority status and month
                .collect(Collectors.groupingBy(
                        t -> t.getDateClosed().format(monthFormatter),
                        Collectors.groupingBy(
                        t -> t.getPriority().getName(),
                        Collectors.averagingLong(t ->
                                Duration.between(t.getDateOpened(), t.getDateClosed()).toMinutes()
                        )
                )));
    }
}