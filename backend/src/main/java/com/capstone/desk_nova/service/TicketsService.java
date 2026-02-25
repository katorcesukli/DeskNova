package com.capstone.desk_nova.service;

import com.capstone.desk_nova.dto.pagination.PaginatedResponse;
import com.capstone.desk_nova.dto.person.PersonResponse;
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
//        Users currentUser = authService.getCurrentAuthenticatedUser();

        // for testing
        Users currentUser = new Users();
        currentUser.setId(1L);
        currentUser.setFirstName("Dummy");
        currentUser.setLastName("User");
        currentUser.setRole(Roles.ADMIN);
        currentUser.setCreatedAt(LocalDateTime.now());

        Page<Tickets> pagedTickets = switch (currentUser.getRole()) {
            case ADMIN -> this.ticketsRepository.findAll(pageable);
            case AGENT -> this.ticketsRepository.findByAgent_Id(currentUser.getId(), pageable);
            case CLIENT ->  this.ticketsRepository.findByClient_Id(currentUser.getId(), pageable);
        };


        List<TicketResponse> tickets = pagedTickets.getContent().stream()
                .map(t -> new TicketResponse(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getCategory().name(),
                        t.getStatus().name(),
                        t.getPriority().getName(),
                        PersonResponse.from(t.getClient()),

                        // handle tickets with no assigned agents
                        Optional.ofNullable(t.getAgent()).map(a -> PersonResponse.from(t.getAgent())).orElse(null),
                        t.getComments().stream().map(c -> new TicketCommentResponse(
                                c.getId(),
                                new PersonResponse(c.getUserId().getFullName(), c.getUserId().getEmail()),
                                c.getComment(),
                                c.getCreatedAt()
                        )).toList(),
                        t.getDateOpened(),
                        t.getDateClosed(),
                        t.getAssignedAt(),
                        t.getUpdatedAt()
                )).toList();

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
        // Users currentUser = authService.getCurrentAuthenticatedUser();

        // for testing
        Users currentUser = new Users();
        currentUser.setId(1L);
        currentUser.setFirstName("Dummy");
        currentUser.setLastName("User");
        currentUser.setRole(Roles.ADMIN);
        currentUser.setCreatedAt(LocalDateTime.now());


        Tickets t =  switch (currentUser.getRole()) {
            case ADMIN -> this.ticketsRepository.findById(ticketId)
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
            case AGENT -> this.ticketsRepository.findByIdAndAgent_Id(ticketId, currentUser.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
            case CLIENT ->  this.ticketsRepository.findByIdAndClient_Id(ticketId, currentUser.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        };

        return new TicketResponse(
            t.getId(),
            t.getTitle(),
            t.getDescription(),
            t.getCategory().name(),
            t.getStatus().name(),
            t.getPriority().getName(),
            PersonResponse.from(t.getClient()),
            Optional.ofNullable(t.getAgent()).map(a -> PersonResponse.from(t.getAgent())).orElse(null),
            t.getComments().stream().map(c -> new TicketCommentResponse(
                    c.getId(),
                    new PersonResponse(c.getUserId().getFullName(), c.getUserId().getEmail()),
                    c.getComment(),
                    c.getCreatedAt()
            )).toList(),
            t.getDateOpened(),
            t.getDateClosed(),
            t.getAssignedAt(),
            t.getUpdatedAt()
        );
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

    public List<AgentWorkloadResponse> getAgentWorkload() {
        return this.ticketsRepository.getAgentWorkloads();
    }

    @Transactional
    public Long createTicket(CreateTicketRequest req) {

        Users exampleClient = new Users();
        exampleClient.setId(13L);
        exampleClient.setFirstName("Dummy");
        exampleClient.setLastName("User");
        exampleClient.setRole(Roles.ADMIN);
        exampleClient.setCreatedAt(LocalDateTime.now());


        Tickets newTicket = new Tickets();
        newTicket.setTitle(req.title());
        newTicket.setDescription(req.description());
        newTicket.setStatus(TicketStatus.valueOf(req.status()));

        TicketPriority priority = this.ticketPriorityRepository.findByName(req.priority())
                .orElseThrow(() -> new EntityNotFoundException("Ticket priority not found. Must be one of the listed option"));

        newTicket.setPriority(priority);
        newTicket.setCategory(TicketCategory.valueOf(req.category()));
        newTicket.setClient(exampleClient);

        // try and assign to the agent with the lowest workload
        return this.ticketsRepository.save(assignTicket(newTicket)).getId();
    }

    public Long editTicket(Long ticketId, EditTicketRequest req) {
        Tickets ticket = this.ticketsRepository.findById(ticketId).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found")
        );

        ticket.setTitle(req.title());
        ticket.setDescription(req.description());
//        ticket.setStatus(TicketStatus.valueOf(req.status()));
        ticket.setCategory(TicketCategory.valueOf(req.category()));
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketsRepository.save(ticket).getId();
    }

    public Long editStatus(Long ticketId, String status ){
        Users currentUser = authService.getCurrentAuthenticatedUser();
        Tickets ticket = this.ticketsRepository.findById(ticketId).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found with id: " + ticketId));

        TicketStatus currentStatus = ticket.getStatus();
        TicketStatus newStatus = TicketStatus.valueOf(status);

        Roles role = currentUser.getRole();

        // validate transition
        if (!isTransitionAllowed(role, currentStatus, newStatus)) {
            throw new RuntimeException(
                    "Role " + role + " cannot change ticket from "
                            + currentStatus + " to " + newStatus);
        }

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        //set closed date

        if (newStatus == TicketStatus.CLOSED) {
            ticket.setDateClosed(LocalDateTime.now());
        }


//        ticket.setStatus(TicketStatus.valueOf(status));
        return ticketsRepository.save(ticket).getId();
    }



    public void deleteTicket(Long ticketId) {
        if(!this.ticketsRepository.existsById(ticketId)) {
            throw new EntityNotFoundException("Ticket not found");
        }

        ticketsRepository.deleteById(ticketId);
    }





    private boolean isTransitionAllowed(Roles role, TicketStatus current, TicketStatus next) {

        return switch (role) {
            case AGENT -> switch (current) {

                case OPEN -> next == TicketStatus.IN_PROGRESS ||
                        next == TicketStatus.RESOLVED;

                case IN_PROGRESS -> next == TicketStatus.RESOLVED;

                case RESOLVED -> false;

                case CLOSED -> false;
            };
            case CLIENT -> switch (current) {

                case OPEN -> next == TicketStatus.CLOSED;

                case IN_PROGRESS -> next == TicketStatus.CLOSED;

                case RESOLVED -> next == TicketStatus.CLOSED ||
                        next == TicketStatus.OPEN;

                case CLOSED -> next == TicketStatus.OPEN;
            };
            case ADMIN -> true;
            default -> false;
        };
    }




}
