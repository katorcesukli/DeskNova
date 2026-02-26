package com.capstone.desk_nova;

import com.capstone.desk_nova.model.TicketPriority;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.Roles;
import com.capstone.desk_nova.model.enums.TicketCategory;
import com.capstone.desk_nova.model.enums.TicketStatus;
import com.capstone.desk_nova.repository.TicketPriorityRepository;
import com.capstone.desk_nova.repository.TicketsRepository;
import com.capstone.desk_nova.repository.UsersRepository;
import com.capstone.desk_nova.service.AuthService;
import com.capstone.desk_nova.service.EmailService;
import com.capstone.desk_nova.service.TicketsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketsServiceTest {

    @Mock private TicketsRepository ticketsRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private AuthService authService;
    @Mock private TicketPriorityRepository ticketPriorityRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private TicketsService ticketsService;

    private Users client;
    private Users agent;
    private TicketPriority priority;

    @BeforeEach
    void setUp() {
        client = new Users();
        client.setId(1L);
        client.setRole(Roles.CLIENT);

        agent = new Users();
        agent.setId(2L);
        agent.setRole(Roles.AGENT);

        priority = new TicketPriority();
        priority.setName("HIGH");
    }

    //
     //
     //AGENTS pa lang na test
   //
    @Nested
    @DisplayName("Status Transition Tests")
    class StatusTests {

        @Test
        @DisplayName("Agent should be able to move OPEN to IN_PROGRESS")
        void editStatus_AgentAllowed() {
            Tickets ticket = new Tickets();
            ticket.setStatus(TicketStatus.OPEN);
            ticket.setAgent(agent);
            ticket.setClient(client);

            when(ticketsRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(authService.getCurrentAuthenticatedUser()).thenReturn(agent);
            when(ticketsRepository.save(any())).thenReturn(ticket);

            ticketsService.editStatus(1L, "IN_PROGRESS");

            assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
        }

        @Test
        @DisplayName("Agent should NOT be able to move RESOLVED to OPEN")
        void editStatus_AgentForbidden() {
            Tickets ticket = new Tickets();
            ticket.setStatus(TicketStatus.RESOLVED);
            ticket.setAgent(agent);

            when(ticketsRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(authService.getCurrentAuthenticatedUser()).thenReturn(agent);

            assertThrows(RuntimeException.class, () -> ticketsService.editStatus(1L, "OPEN"));
        }
    }
}