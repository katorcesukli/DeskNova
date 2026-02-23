package com.capstone.desk_nova.service;

import com.capstone.desk_nova.exceptions.AccountDoesNotExistException;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.repository.TicketsRepository;
import com.capstone.desk_nova.repository.UsersRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Data
@RequiredArgsConstructor
public class TicketsService {

    private final TicketsRepository ticketsRepository;
    private final UsersRepository usersRepository;

    //Ticket View
    public List<Tickets> getAllTickets() {
        return ticketsRepository.findAll();
    }

    public Tickets getTicketsById(Long id) {
        return ticketsRepository.findById(id).orElseThrow(()-> new RuntimeException("error"));
    }

    public Tickets createTicket(Tickets ticket) {
        return ticketsRepository.save(ticket);
    }

    public Tickets updateTicket(Long id, Tickets ticketDetails) {
        Tickets ticket = (Tickets) getTicketsById(id);
        ticket.setTitle(ticketDetails.getTitle());
        ticket.setDescription(ticketDetails.getDescription());
        ticket.setPriority(ticketDetails.getPriority());
        ticket.setStatus(ticketDetails.getStatus());
        ticket.setCategory(ticketDetails.getCategory());
        ticket.setUpdatedAt(ticketDetails.getUpdatedAt());

        return ticketsRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        ticketsRepository.deleteById(id);
    }





}
