package com.capstone.desk_nova.service;

import com.capstone.desk_nova.dto.ticket.TicketCommentRequest;
import com.capstone.desk_nova.model.TicketComments;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.Roles;
import com.capstone.desk_nova.repository.TicketCommentsRepository;
import com.capstone.desk_nova.repository.TicketsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


@Service
@Data
@RequiredArgsConstructor
public class TicketCommentsService {

    private final TicketCommentsRepository ticketCommentsRepository;
    private final TicketsRepository ticketsRepository;
    private final AuthService authService;
    private final EmailService emailService;

    public Long addComment(TicketCommentRequest req) {

        Users currentUser = this.authService.getCurrentAuthenticatedUser();

        Tickets currentTicket = this.ticketsRepository.findById(req.ticketId()).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found")
        );

        // check if CLIENT owns ticket or AGENT is assigned to the ticket
        boolean isAllowed = switch (currentUser.getRole()) {
            case CLIENT -> currentTicket.getClient().getId().equals(currentUser.getId());
            case AGENT -> currentTicket.getAgent().getId().equals(currentUser.getId());
            default -> false;
        };

        if(!isAllowed) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        //method to check commenter
        Users recipient = null;
        if (currentUser.getRole() == Roles.CLIENT) {
            // Client commented -> Notify Agent
            recipient = currentTicket.getAgent();
        } else {
            // Agent or Admin commented -> Notify Client
            recipient = currentTicket.getClient();
        }

        TicketComments newComment = new TicketComments();
        newComment.setUserId(currentUser);
        newComment.setTicket(currentTicket);
        newComment.setComment(req.comment());

        //email block
        if (recipient != null) {
            try {
                emailService.sendCommentNotificationEmail(recipient, currentUser, currentTicket, req.comment());
            } catch (Exception e) {
                // Log and continue - don't crash the comment save if email fails
                System.err.println("Comment email notification failed: " + e.getMessage());
            }
        }

        return ticketCommentsRepository.save(newComment).getId();
    }

    public Long editTaskComment(Long ticketCommentId, TicketCommentRequest req) {
        TicketComments comment = this.ticketCommentsRepository.findById(ticketCommentId).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found")
        );

        Users currentUser = this.authService.getCurrentAuthenticatedUser();

        if(!currentUser.getId().equals(comment.getUserId().getId())) {
            throw new AccessDeniedException("You are not allowed to edit this comment");
        }

        comment.setComment(req.comment());

        return this.ticketCommentsRepository.save(comment).getId();
    }

    public void deleteComment(Long id) {
        TicketComments comment = this.ticketCommentsRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Ticket not found")
        );

        boolean isAllowed = switch(comment.getTicket().getStatus()){
            case RESOLVED, CLOSED -> false;
            default -> true;
        };


        // add check for deletion of closed or resolved tickets
        if(!isAllowed){
            throw new IllegalStateException("Closed or resolved tickets cannot be deleted");
        }

        Users currentUser = this.authService.getCurrentAuthenticatedUser();

        if(!currentUser.getId().equals(comment.getUserId().getId())) {
            throw new AccessDeniedException("You are not allowed to delete this comment");
        }

        ticketCommentsRepository.deleteById(id);
    }

}
