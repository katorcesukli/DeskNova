package com.capstone.desk_nova;

import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.repository.UsersRepository;
import com.capstone.desk_nova.service.EmailService;
import com.capstone.desk_nova.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {


    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private UsersService usersService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setEmail("test@gmail.com");
        testUser.setPassword("123");
        testUser.setFirstName("Rian");
        testUser.setLastName("Miguel");

        emailService = new EmailService(mailSender);
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
        assertEquals("Welcome to Desk Nova!", sentMessage.getSubject());

        String body = sentMessage.getText();
        assert body != null;
        assertTrue(body.contains("Hello Rian"));
        assertTrue(body.contains("account has been created successfully"));
    }
}
