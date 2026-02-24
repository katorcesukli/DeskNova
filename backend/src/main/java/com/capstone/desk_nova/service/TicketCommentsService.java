package com.capstone.desk_nova.service;

import com.capstone.desk_nova.dto.ticket.TicketCommentRequest;
import com.capstone.desk_nova.model.TicketComments;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.Roles;
import com.capstone.desk_nova.repository.TicketCommentsRepository;
import com.capstone.desk_nova.repository.TicketsRepository;
import com.capstone.desk_nova.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class TicketCommentsService {

    private final TicketCommentsRepository ticketCommentsRepository;
    private final UsersRepository usersRepository;
    private final TicketsRepository ticketsRepository;

    public List<TicketComments> getCommentsByTicket(Long ticketId) {
        return ticketCommentsRepository.findByTicketId(ticketId);
    }

    public Long addComment(TicketCommentRequest req) {

        Users exampleClient = new Users();
        exampleClient.setId(1L);
        exampleClient.setFirstName("Dummy");
        exampleClient.setLastName("User");
        exampleClient.setRole(Roles.CLIENT);
        exampleClient.setCreatedAt(LocalDateTime.now());

        Tickets currentTicket = this.ticketsRepository.findById(req.id()).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found")
        );

        TicketComments newComment = new TicketComments();
        newComment.setUserId(exampleClient);
        newComment.setTicket(currentTicket);
        newComment.setComment(req.comment());
        return ticketCommentsRepository.save(newComment).getId();
    }

    public Long editTaskComment(Long ticketCommentId, TicketCommentRequest req) {
        TicketComments currentTicket = this.ticketCommentsRepository.findById(ticketCommentId).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found")
        );

        currentTicket.setComment(req.comment());

        return this.ticketCommentsRepository.save(currentTicket).getId();
    }

    public void deleteComment(Long id) {
        if(!this.ticketCommentsRepository.existsById(id)) {
            throw new EntityNotFoundException("Ticket not found");
        }
        ticketCommentsRepository.deleteById(id);
    }

}
