package com.capstone.desk_nova;

import com.capstone.desk_nova.dto.ticket.TicketCommentRequest;
import com.capstone.desk_nova.model.TicketComments;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.Roles;
import com.capstone.desk_nova.repository.TicketCommentsRepository;
import com.capstone.desk_nova.repository.TicketsRepository;
import com.capstone.desk_nova.service.AuthService;
import com.capstone.desk_nova.service.TicketCommentsService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketCommentsServiceTest {

    @Mock private TicketCommentsRepository ticketCommentsRepository;
    @Mock private TicketsRepository ticketsRepository;
    @Mock private AuthService authService;

    @InjectMocks
    private TicketCommentsService ticketCommentsService;

    private Users clientUser;
    private Users agentUser;
    private Tickets ticket;
    private TicketCommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        clientUser = new Users();
        clientUser.setId(1L);
        clientUser.setRole(Roles.CLIENT);

        agentUser = new Users();
        agentUser.setId(2L);
        agentUser.setRole(Roles.AGENT);

        ticket = new Tickets();
        ticket.setId(100L);
        ticket.setClient(clientUser);
        ticket.setAgent(agentUser);

        commentRequest = new TicketCommentRequest(100L, "This is a test comment");
    }

    @Nested
    @DisplayName("Add Comment Tests")
    class AddCommentTests {

        @Test
        @DisplayName("Should add comment when CLIENT is the ticket owner")
        void addComment_ClientSuccess() {
            when(authService.getCurrentAuthenticatedUser()).thenReturn(clientUser);
            when(ticketsRepository.findById(100L)).thenReturn(Optional.of(ticket));

            TicketComments savedComment = new TicketComments();
            savedComment.setId(500L);
            when(ticketCommentsRepository.save(any(TicketComments.class))).thenReturn(savedComment);

            Long resultId = ticketCommentsService.addComment(commentRequest);

            assertEquals(500L, resultId);
            verify(ticketCommentsRepository).save(any(TicketComments.class));
        }

        @Test
        @DisplayName("Should throw AccessDenied when a CLIENT tries to comment on someone else's ticket")
        void addComment_ClientForbidden() {
            Users wrongClient = new Users();
            wrongClient.setId(999L);
            wrongClient.setRole(Roles.CLIENT);

            when(authService.getCurrentAuthenticatedUser()).thenReturn(wrongClient);
            when(ticketsRepository.findById(100L)).thenReturn(Optional.of(ticket));

            assertThrows(AccessDeniedException.class, () -> ticketCommentsService.addComment(commentRequest));
        }
    }

    @Nested
    @DisplayName("Edit Comment Tests")
    class EditCommentTests {

        @Test
        @DisplayName("Should edit comment when user is the author")
        void editComment_Success() {
            TicketComments existingComment = new TicketComments();
            existingComment.setId(1L);
            existingComment.setUserId(clientUser);
            existingComment.setComment("Old Comment");

            when(ticketCommentsRepository.findById(1L)).thenReturn(Optional.of(existingComment));
            when(authService.getCurrentAuthenticatedUser()).thenReturn(clientUser);
            when(ticketCommentsRepository.save(any(TicketComments.class))).thenReturn(existingComment);

            Long resultId = ticketCommentsService.editTaskComment(1L, new TicketCommentRequest(100L, "Updated Comment"));

            assertEquals(1L, resultId);
            assertEquals("Updated Comment", existingComment.getComment());
        }

        @Test
        @DisplayName("Should throw AccessDenied when user tries to edit another user's comment")
        void editComment_Forbidden() {
            TicketComments existingComment = new TicketComments();
            existingComment.setId(1L);
            existingComment.setUserId(agentUser); // Authored by Agent

            when(ticketCommentsRepository.findById(1L)).thenReturn(Optional.of(existingComment));
            when(authService.getCurrentAuthenticatedUser()).thenReturn(clientUser); // Current user is Client

            assertThrows(AccessDeniedException.class, () ->
                    ticketCommentsService.editTaskComment(1L, commentRequest));
        }
    }

    @Nested
    @DisplayName("Delete Comment Tests")
    class DeleteCommentTests {

        @Test
        @DisplayName("Should delete comment successfully")
        void deleteComment_Success() {
            TicketComments existingComment = new TicketComments();
            existingComment.setUserId(clientUser);

            when(ticketCommentsRepository.findById(1L)).thenReturn(Optional.of(existingComment));
            when(authService.getCurrentAuthenticatedUser()).thenReturn(clientUser);

            ticketCommentsService.deleteComment(1L);

            verify(ticketCommentsRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFound when comment doesn't exist")
        void deleteComment_NotFound() {
            when(ticketCommentsRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> ticketCommentsService.deleteComment(1L));
        }
    }
}