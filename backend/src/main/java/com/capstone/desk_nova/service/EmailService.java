package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import jakarta.mail.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String TOKEN = "8bd4d4cff9df4fcd14ad043732b42438";

    //upon registration, send a notif email
    public void sendWelcomeEmail(Users user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mail.dexnova.noreply");
        message.setTo(user.getEmail());
        message.setSubject("Welcome to Desk Nova!");
        message.setText("Hello " + user.getFirstName() + ",\n\n" +
                "Your account has been created successfully. You can now log in and manage your support tickets."
                );

        mailSender.send(message);
    }

    public void sendTicketAssignmentEmail(Users agent, Tickets ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mail.dexnova.noreply");
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

    public void sendTicketUpdateEmail(Users client, Tickets ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mail.dexnova.noreply");
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

    public void sendTicketResolvedEmail(Users client, Users agent, Tickets ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mail.dexnova.noreply");
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

    public void sendTicketClosedEmail(Users client, Tickets ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mail.dexnova.noreply");
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
    public void sendTestEmail(Users user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mail.dexnova.noreply");
        message.setTo("Test@gmail.com");
        message.setSubject("Welcome to Desk Nova!");
        message.setText("Hello " + user.getFirstName() + ",\n\n" +
                "Your account has been created successfully. You can now log in and manage your support tickets."
        );

        mailSender.send(message);
    }

    public void sendNotification(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
