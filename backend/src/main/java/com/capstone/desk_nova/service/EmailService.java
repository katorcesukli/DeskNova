package com.capstone.desk_nova.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTicketCommentNotification(String toEmail, String ticketTitle, String comment, String commenter) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("New Comment on Ticket: " + ticketTitle);

        message.setText(
                "Hello,\n\n" +
                        commenter + " commented on your ticket.\n\n" +
                        "Comment:\n" + comment + "\n\n" +
                        "Please login to view the full ticket.\n\n" +
                        "Helpdesk System"
        );

        mailSender.send(message);
    }
}