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

    public Optional<Tickets> getTicketsById(Long id) {
        return ticketsRepository.getByTicketId(id);
    }

    public Tickets createNewTicket(Tickets tickets, Long userId) {
        Users users = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found: " + userId));
        tickets.setTicketId(userId);
        //set date
        if (tickets.getAssignedAt() == null) {
            tickets.setAssignedAt(LocalDate.now());
        }
        return ticketsRepository.save(tickets);
    }

    public Tickets deleteTicket(Long userId, Long ticketId){
        Users users = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found: " + userId));
        Tickets tickets = (Tickets) ticketsRepository.getByTicketId();
    }


}
