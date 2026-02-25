package com.capstone.desk_nova.service;

import com.capstone.desk_nova.dto.pagination.PaginatedResponse;
import com.capstone.desk_nova.dto.ticket.*;
import com.capstone.desk_nova.model.TicketPriority;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.Roles;
import com.capstone.desk_nova.model.enums.TicketCategory;
import com.capstone.desk_nova.model.enums.TicketStatus;
import com.capstone.desk_nova.repository.TicketPriorityRepository;
import com.capstone.desk_nova.repository.TicketsRepository;
import com.capstone.desk_nova.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Data
@RequiredArgsConstructor
public class TicketsService {

    private final TicketsRepository ticketsRepository;
    private final UsersRepository usersRepository;
    private final AuthService authService;
    private final TicketPriorityRepository ticketPriorityRepository;

    public PaginatedResponse<TicketResponse> getAllTickets(Pageable pageable) {
        Users currentUser = authService.getCurrentAuthenticatedUser();

        Page<Tickets> pagedTickets = switch (currentUser.getRole()) {
            case ADMIN -> this.ticketsRepository.findAll(pageable);
            case AGENT -> this.ticketsRepository.findByAgent_Id(currentUser.getId(), pageable);
            case CLIENT ->  this.ticketsRepository.findByClient_Id(currentUser.getId(), pageable);
        };

        List<TicketResponse> tickets = pagedTickets.getContent()
                .stream()
                .map(TicketResponse::from).toList();

        return new PaginatedResponse<>(
                tickets,
                pagedTickets.getNumber(),
                pagedTickets.getTotalPages(),
                pagedTickets.getTotalElements(),
                pageable.getPageSize(),
                pagedTickets.hasNext(),
                pageable.hasPrevious()
        );
    }

    public TicketResponse getTicketById(Long ticketId) {
        Users currentUser = authService.getCurrentAuthenticatedUser();


        // [CONSIDER] create fetch methods for each role to have more control in handling exceptions
        // e.g. differentiate "Ticket not found", "Ticket not assigned", and "Agent not found"

        Tickets ticket =  switch (currentUser.getRole()) {
            case ADMIN -> this.ticketsRepository.findById(ticketId)
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
            case AGENT -> this.ticketsRepository.findByIdAndAgent_Id(ticketId, currentUser.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
            case CLIENT ->  this.ticketsRepository.findByIdAndClient_Id(ticketId, currentUser.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        };

        return TicketResponse.from(ticket);
    }

    public Tickets assignTicket(Tickets ticket) {
        Long agentIdAssignee = this.ticketsRepository.getAgentWorkloads().getFirst().agentId();

        if(Optional.ofNullable(agentIdAssignee).isEmpty()) {
            throw new EntityNotFoundException("There are no agents available");
        }

        //[CONSIDER]: add check for possible "WIP limit" on the agent
        // if WIP limit has been exceeded in all agents
        // put ticket 'ON-HOLD' if priority is lower than 'MEDIUM'

        Users agent = this.usersRepository.findById(agentIdAssignee).orElseThrow(() -> new EntityNotFoundException("Agent not found"));
        ticket.setAgent(agent);
        ticket.setAssignedAt(LocalDateTime.now());

        return ticket;
    }

    @Transactional
    public Long createTicket(CreateTicketRequest req) {
        Users currentUser = this.authService.getCurrentAuthenticatedUser();

        Tickets newTicket = new Tickets();
        newTicket.setTitle(req.title());
        newTicket.setDescription(req.description());
        newTicket.setStatus(TicketStatus.valueOf(req.status()));

        TicketPriority priority = this.ticketPriorityRepository.findByName(req.priority())
                .orElseThrow(() -> new EntityNotFoundException("Ticket priority not found. Must be one of the listed option"));

        newTicket.setPriority(priority);
        newTicket.setCategory(TicketCategory.valueOf(req.category()));
        newTicket.setClient(currentUser);

        // try and assign ticket to the agent with the lowest workload
        return this.ticketsRepository.save(assignTicket(newTicket)).getId();
    }

    public Long editTicket(Long ticketId, EditTicketRequest req) {

        Tickets ticket = this.ticketsRepository.findById(ticketId).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found")
        );

        Users currentUser = this.authService.getCurrentAuthenticatedUser();

        // check if the current user is a 'CLIENT' and the one who created the ticket
        if(
            currentUser.getRole().equals(Roles.CLIENT) &&
            !currentUser.getId().equals(ticket.getClient().getId()))
        {
            throw new AccessDeniedException("Unauthorized to edit this ticket");
        }

        ticket.setTitle(req.title());
        ticket.setDescription(req.description());
        ticket.setCategory(TicketCategory.valueOf(req.category()));
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketsRepository.save(ticket).getId();
    }

    public Long editStatus(Long ticketId, String status ){

        Tickets ticket = this.ticketsRepository.findById(ticketId).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found with id: " + ticketId));

        Users currentUser = this.authService.getCurrentAuthenticatedUser();

        Roles role = currentUser.getRole();

        // check if the current user is either a 'CLIENT' and the one who created the ticket
        // or current user is an ADMIN
        if (role == Roles.CLIENT &&
                !currentUser.getId().equals(ticket.getClient().getId())) {

            throw new AccessDeniedException(
                    "You can only modify your own tickets");
        }

        // AGENT can only modify assigned tickets
        if (role == Roles.AGENT &&
                (ticket.getAgent() == null ||
                        !ticket.getAgent().getId().equals(currentUser.getId()))) {

            throw new AccessDeniedException(
                    "You can only modify tickets assigned to you");
        }

//        ticket.setStatus(TicketStatus.valueOf(status));
        TicketStatus currentStatus = ticket.getStatus();
        TicketStatus newStatus = TicketStatus.valueOf(status);

//        Roles role = currentUser.getRole();

        // validate transition
        if (!isTransitionAllowed(role, currentStatus, newStatus)) {
            throw new AccessDeniedException(
                    "Role " + role + " cannot change ticket from "
                            + currentStatus + " to " + newStatus);
        }

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        //update date and time after status change
        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setDateResolved(LocalDateTime.now());
        }
        if (newStatus == TicketStatus.CLOSED) {
            ticket.setDateClosed(LocalDateTime.now());
        }

        return ticketsRepository.save(ticket).getId();
    }

    public void deleteTicket(Long ticketId) {
        Tickets ticket = this.ticketsRepository.findById(ticketId).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found")
        );

        Users currentUser = this.authService.getCurrentAuthenticatedUser();

        // check if the current user is either a 'CLIENT' and the one who created the ticket
        // or current user is an ADMIN
        if(
                (currentUser.getRole().equals(Roles.CLIENT) &&
                !currentUser.getId().equals(ticket.getClient().getId()))
                || currentUser.getRole().equals(Roles.ADMIN)
        )
        {
            throw new AccessDeniedException("Unauthorized to delete this ticket");
        }

        ticketsRepository.delete(ticket);
    }

    private boolean isTransitionAllowed(Roles role, TicketStatus current, TicketStatus next) {

        // prevent same status update
        if (current == next) {
            return false;
        }

        return switch (role) {
            case AGENT -> switch (current) {

                case OPEN -> next == TicketStatus.IN_PROGRESS ||
                        next == TicketStatus.RESOLVED;

                case IN_PROGRESS -> next == TicketStatus.RESOLVED;

                case RESOLVED -> false;

                case CLOSED -> false;
            };
            case CLIENT -> switch (current) {

                case OPEN, IN_PROGRESS -> next == TicketStatus.CLOSED;

                case RESOLVED -> next == TicketStatus.CLOSED ||
                        next == TicketStatus.OPEN;

                case CLOSED -> next == TicketStatus.OPEN;
            };
            case ADMIN -> true;
        };
    }
}
