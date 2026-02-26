package com.capstone.desk_nova;

import com.capstone.desk_nova.dto.ticket.TicketMetric;
import com.capstone.desk_nova.model.TicketPriority;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.TicketStatus;
import com.capstone.desk_nova.repository.TicketsRepository;
import com.capstone.desk_nova.service.MetricService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricServiceTest {

    @Mock
    private TicketsRepository ticketsRepository;

    @InjectMocks
    private MetricService metricService;

    private Tickets ticket1;
    private Tickets ticket2;

    @BeforeEach
    void setUp() {

        Users agent = new Users();
        agent.setFirstName("Rian");
        agent.setLastName("Miguel");

        TicketPriority highPriority = new TicketPriority();
        highPriority.setName("HIGH");

        ticket1 = new Tickets();
        ticket1.setStatus(TicketStatus.RESOLVED);
        ticket1.setDateOpened(LocalDateTime.now().minusHours(2));
        ticket1.setDateResolved(LocalDateTime.now().minusHours(1));
        ticket1.setAgent(agent);
        ticket1.setPriority(highPriority);

        ticket2 = new Tickets();
        ticket2.setStatus(TicketStatus.OPEN);
        ticket2.setDateOpened(LocalDateTime.now());
        ticket2.setPriority(highPriority);
    }

    @Test
    @DisplayName("Should calculate admin metrics correctly")
    void getAdminMetrics_ShouldCalculateCorrectly() {
        List<Tickets> ticketsList = Arrays.asList(ticket1, ticket2);
        when(ticketsRepository.findAll()).thenReturn(ticketsList);

        TicketMetric metrics = metricService.getAdminMetrics();

        assertEquals(2, metrics.totalTickets());
        assertEquals(60.0, metrics.avgResolutionTimeMinutes());
        assertEquals(50.0, metrics.completionRate());

        assertNotNull(metrics.ticketsByStatus());
        assertTrue(metrics.ticketsByStatus().containsKey("RESOLVED"));
        assertTrue(metrics.ticketsByStatus().containsKey("OPEN"));
        assertEquals(1, metrics.ticketsByStatus().get("RESOLVED"));

        assertNotNull(metrics.agentPerformance());
        assertTrue(metrics.agentPerformance().containsKey("Rian Miguel"), "Should contain Rian Miguel");
        assertEquals(60.0, metrics.agentPerformance().get("Rian Miguel"));
    }

    @Test
    @DisplayName("Should return zero metrics when no tickets exist")
    void getAdminMetrics_EmptyList_ShouldReturnZero() {
        when(ticketsRepository.findAll()).thenReturn(List.of());

        TicketMetric metrics = metricService.getAdminMetrics();

        assertEquals(0, metrics.totalTickets());
        assertEquals(0.0, metrics.avgResolutionTimeMinutes());
        assertEquals(0.0, metrics.completionRate());
        assertTrue(metrics.ticketsByStatus().isEmpty());
    }

    @Test
    @DisplayName("Should calculate average resolution per priority and month")
    void getAveResolvePerPriority_ShouldGroupCorrectly() {
        when(ticketsRepository.findAll()).thenReturn(List.of(ticket1));

        Map<String, Map<String, Double>> result = metricService.getAveResolvePerPriority();

        assertFalse(result.isEmpty());

        String currentMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM yyyy"));
        assertTrue(result.containsKey(currentMonth), "Result should contain key: " + currentMonth);

        assertNotNull(result.get(currentMonth));
        assertEquals(60.0, result.get(currentMonth).get("HIGH"));
    }
}