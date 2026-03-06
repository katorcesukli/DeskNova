package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import jakarta.mail.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private final JavaMailSender mailSender;

    private static final String TOKEN = "8bd4d4cff9df4fcd14ad043732b42438";

    //upon registration, send a notif email
    @Async
    public void sendWelcomeEmail(Users user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("desk.nova.info@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Welcome to Desk Nova!");
        message.setText("Hello " + user.getFirstName() + ",\n\n" +
                "Your account has been created successfully. You can now log in and manage your support tickets."
                );

        mailSender.send(message);
    }

    @Async
    public void sendTicketAssignmentEmail(Users agent, Tickets ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("desk.nova.info@gmail.com");
        message.setTo(agent.getEmail());
        message.setSubject("New Ticket Assigned: [" + ticket.getPriority().getName() + "] #" + ticket.getId());

        String content = String.format(
                "Hello %s,\n\n" +
                        "A new ticket has been assigned to you.\n\n" +
                        "Ticket ID: #%d\n" +
                        "Title: %s\n" +
                        "Priority: %s\n" +
                        "Category: %s\n" +
                        "Description: %s\n\n" +
                        "Please log in to the DeskNova dashboard to manage this request.",
                agent.getFirstName(),
                ticket.getId(),
                ticket.getTitle(),
                ticket.getPriority().getName(),
                ticket.getCategory(),
                ticket.getDescription()
        );

        message.setText(content);
        mailSender.send(message);
    }

    @Async
    public void sendTicketUpdateEmail(Users client, Tickets ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("desk.nova.info@gmail.com");
        message.setTo(client.getEmail());
        message.setSubject("Update Ticket Assigned: [" + ticket.getPriority().getName() + "] #" + ticket.getId());

        String content = String.format(
                "Hello %s,\n\n" +
                        "An agent has an update on a ticket below.\n\n" +
                        "Ticket ID: #%d\n" +
                        "Title: %s\n" +
                        "Priority: %s\n" +
                        "Category: %s\n" +
                        "Description: %s\n\n" +
                        "Please log in to the DeskNova dashboard to manage this request.",
                client.getFirstName(),
                ticket.getId(),
                ticket.getTitle(),
                ticket.getPriority().getName(),
                ticket.getCategory(),
                ticket.getDescription()
        );

        message.setText(content);
        mailSender.send(message);
    }

    @Async
    public void sendTicketResolvedEmail(Users client, Users agent, Tickets ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("desk.nova.info@gmail.com");
        message.setTo(client.getEmail(), agent.getEmail());
        message.setSubject("Resolved Ticket Assigned: [" + ticket.getPriority().getName() + "] #" + ticket.getId());

        String content = String.format(
                "Hello to whom it may concern,\n\n" +
                        "The ticket below has been resolved.\n\n" +
                        "Ticket ID: #%d\n" +
                        "Title: %s\n" +
                        "Priority: %s\n" +
                        "Category: %s\n" +
                        "Description: %s\n\n" +
                        "Thank you for using our application.",

                ticket.getId(),
                ticket.getTitle(),
                ticket.getPriority().getName(),
                ticket.getCategory(),
                ticket.getDescription()
        );

        message.setText(content);
        mailSender.send(message);
    }

    @Async
    public void sendTicketClosedEmail(Users client, Tickets ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("desk.nova.info@gmail.com");
        message.setTo(client.getEmail());
        message.setSubject("Closed Ticket Assigned: [" + ticket.getPriority().getName() + "] #" + ticket.getId());

        String content = String.format(
                "Hello %s ,\n\n" +
                        "The ticket below has been closed.\n\n" +
                        "Ticket ID: #%d\n" +
                        "Title: %s\n" +
                        "Priority: %s\n" +
                        "Category: %s\n" +
                        "Description: %s\n\n" +
                        "Upon review you can set it to Open or Resolved.",
                client.getFirstName(),
                ticket.getId(),
                ticket.getTitle(),
                ticket.getPriority().getName(),
                ticket.getCategory(),
                ticket.getDescription()
        );

        message.setText(content);
        mailSender.send(message);
    }

    @Async
    public void sendCommentNotificationEmail(Users recipient, Users commenter, Tickets ticket, String commentText) {
        if (recipient == null || recipient.getEmail() == null) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("desk.nova.info@gmail.com");
        message.setTo(recipient.getEmail());
        message.setSubject("New Comment on Ticket #: " + ticket.getId() + " [" + ticket.getTitle() + "]");

        String content = String.format(
                "Hello %s,\n\n" +
                        "%s %s has added a new comment to ticket #%d:\n\n" +
                        "\"%s\"\n\n" +
                        "Priority: %s\n" +
                        "Status: %s\n\n" +
                        "Please log in to desknova to resolve this issue.",
                recipient.getFirstName(),
                commenter.getFirstName(),
                commenter.getLastName(),
                ticket.getId(),
                commentText,
                ticket.getPriority().getName(),
                ticket.getStatus()
        );

        message.setText(content);
        mailSender.send(message);
    }

    @Async
    public void sendTestEmail(Users user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("desk.nova.info@gmail.com");
        message.setTo("Test@gmail.com");
        message.setSubject("Welcome to Desk Nova!");
        message.setText("Hello " + user.getFirstName() + ",\n\n" +
                "Your account has been created successfully. You can now log in and manage your support tickets."
        );

        mailSender.send(message);
    }

}
