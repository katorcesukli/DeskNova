//package com.capstone.desk_nova.service;
//
//import com.capstone.desk_nova.model.TicketComments;
//import com.capstone.desk_nova.model.Tickets;
//import com.capstone.desk_nova.model.Users;
//import com.capstone.desk_nova.repository.TicketCommentsRepository;
//import com.capstone.desk_nova.repository.TicketsRepository;
//import com.capstone.desk_nova.repository.UsersRepository;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@Data
//@RequiredArgsConstructor
//public class TicketCommentsService {
//
//    private final TicketCommentsRepository ticketCommentsRepository;
//    private final TicketsRepository ticketsRepository;
//    private final UsersRepository usersRepository;
//    private final EmailService emailService;
//
//    public List<TicketComments> getCommentsByTicketId(Long ticketId) {
//        return ticketCommentsRepository.findByTicketId(ticketId);
//    }
//
//    public TicketComments addComment(TicketComments comment) {
//        return ticketCommentsRepository.save(comment);
//    }
//
//    public void deleteComment(Long id) {
//        ticketCommentsRepository.deleteById(id);
//    }
//
//    public TicketComments addComment(Long ticketId, Long userId, String text) {
//
//        // Validate Ticket
//        Tickets ticket = ticketsRepository.findById(ticketId)
//                .orElseThrow(() -> new RuntimeException("Ticket not found"));
//
//        // Validate User
//        Users user = usersRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Create Comment
//        Tickets ticketComments = new Tickets();
//
//        ticketComments.setTicket(ticket);
//        ticketComments.setUser(user);
//        ticketComments.setCommentText(text);
//        ticketComments.setCreatedAt(LocalDateTime.now());
//
//        // Save Comment
//        TicketComments savedComment = ticketCommentsRepository.save(ticketComment);
//
//        // Send Email Notification
//        sendEmailNotifications(ticket, user, text);
//
//        return savedComment;
//    }
//
//
//    private void sendEmailNotifications(Tickets ticket, Users commenter, String commentText) {
//
//        String subjectTicket = ticket.getTitle();
//        String commenterName = commenter.getName();
//
//        // Notify ticket creator
//        if (ticket.getCreatedBy() != null) {
//            emailService.sendTicketCommentNotification(
//                    ticket.getCreatedBy().getEmail(),
//                    subjectTicket,
//                    commentText,
//                    commenterName
//            );
//        }
//
//        // Notify assigned agent (if exists)
//        if (ticket.getAssignedAgent() != null) {
//            emailService.sendTicketCommentNotification(
//                    ticket.getAssignedAgent().getEmail(),
//                    subjectTicket,
//                    commentText,
//                    commenterName
//            );
//        }
//    }
//
//
//}
