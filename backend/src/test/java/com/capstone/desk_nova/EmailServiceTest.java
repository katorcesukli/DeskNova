package com.capstone.desk_nova;

import com.capstone.desk_nova.model.TicketPriority;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.TicketCategory;
import com.capstone.desk_nova.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    private Users testUser;
    private Users client;
    private Users agent;
    private Tickets ticket;

    @BeforeEach
    void setUp() {

        emailService = new EmailService(mailSender);

        testUser = new Users();
        testUser.setEmail("test@gmail.com");
        testUser.setFirstName("Rian");

        client = new Users();
        client.setEmail("client@test.com");
        client.setFirstName("JM");

        agent = new Users();
        agent.setEmail("agent@test.com");
        agent.setFirstName("Jed");

        TicketPriority priority = new TicketPriority();
        priority.setName("High");

        ticket = new Tickets();
        ticket.setId(101L);
        ticket.setTitle("Network Issue");
        ticket.setPriority(priority);
        ticket.setCategory(TicketCategory.valueOf("HARDWARE"));
        ticket.setDescription("NO WIFI");
    }

    @Test
    @DisplayName("Should construct and send the correct welcome email message")
    void sendWelcomeEmail_ShouldSendCorrectDetails() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendWelcomeEmail(testUser);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals("mail.dexnova.noreply", sentMessage.getFrom());
        assertEquals("test@gmail.com", Objects.requireNonNull(sentMessage.getTo())[0]);
        assertTrue(sentMessage.getText().contains("Hello Rian"));
    }

    @Test
    @DisplayName("Should send resolved email to both client and agent")
    void testSendTicketResolvedEmail_MultipleRecipients() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendTicketResolvedEmail(client, agent, ticket);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        String[] recipients = msg.getTo();
        assertNotNull(recipients, "Recipients list should not be null");
        assertTrue(Arrays.asList(recipients).contains("client@test.com"));
        assertTrue(Arrays.asList(recipients).contains("agent@test.com"));
        assertTrue(msg.getText().contains("has been resolved"));
    }

    @Test
    @DisplayName("Should send closed email to client")
    void testSendTicketClosedEmail() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendTicketClosedEmail(client, ticket);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertEquals("client@test.com", Objects.requireNonNull(msg.getTo())[0]);
        assertTrue(msg.getText().contains("has been closed"));
    }
}