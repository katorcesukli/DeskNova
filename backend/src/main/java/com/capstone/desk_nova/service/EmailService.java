package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    public void sendWelcomeEmail(Users user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
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
