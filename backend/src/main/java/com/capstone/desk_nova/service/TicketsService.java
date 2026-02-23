package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.repository.TicketsRepository;
import com.capstone.desk_nova.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return ticketsRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Ticket with" + id + " does not exist"));
    }

    public Tickets createTicket(Tickets ticket) {
        return ticketsRepository.save(ticket);
    }

    //to be updated
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
