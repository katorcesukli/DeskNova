package com.capstone.desk_nova;

import com.capstone.desk_nova.dto.auth.AuthResponse;
import com.capstone.desk_nova.dto.auth.LoginRequest;
import com.capstone.desk_nova.dto.auth.RegisterRequest;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.Roles;
import com.capstone.desk_nova.repository.UsersRepository;
import com.capstone.desk_nova.security.JwtUtil;
import com.capstone.desk_nova.service.AuthService;
import com.capstone.desk_nova.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsersRepository usersRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private Users testUser;
    private final String MOCK_TOKEN = "mock.jwt.token";

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setEmail("user@test.com");
        testUser.setFirstName("Rian");
        testUser.setLastName("Miguel");
        testUser.setRole(Roles.CLIENT);
        testUser.setPassword("encodedPassword");
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {
        @Test
        void register_Success() {
            RegisterRequest request = new RegisterRequest("Rian", "Miguel", "user@test.com", "password123");

            when(usersRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(usersRepository.save(any(Users.class))).thenReturn(testUser);
            when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString())).thenReturn(MOCK_TOKEN);

            AuthResponse response = authService.register(request);

            assertNotNull(response);
            verify(usersRepository).save(any(Users.class));
            verify(emailService).sendWelcomeEmail(any(Users.class));
        }

        @Test
        void register_ThrowsDuplicateKeyException_WhenEmailExists() {
            RegisterRequest request = new RegisterRequest("Rian", "Miguel", "user@test.com", "password123");
            when(usersRepository.existsByEmail(request.email())).thenReturn(true);

            assertThrows(DuplicateKeyException.class, () -> authService.register(request));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {
        @Test
        void login_Success() {
            LoginRequest request = new LoginRequest("user@test.com", "password123");

            when(usersRepository.findByEmail(request.email())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
            when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyString())).thenReturn(MOCK_TOKEN);

            AuthResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals("user@test.com", testUser.getEmail());
        }

        @Test
        void login_ThrowsSecurityException_WhenPasswordInvalid() {
            LoginRequest request = new LoginRequest("user@test.com", "wrongPassword");

            when(usersRepository.findByEmail(request.email())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

            assertThrows(SecurityException.class, () -> authService.login(request));
        }
    }

    @Nested
    @DisplayName("Security Context Tests")
    class SecurityContextTests {
        @Test
        @DisplayName("Should return user when authenticated")
        void getCurrentAuthenticatedUser_Success() {
            // Mocking the static SecurityContext
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("user@test.com");
            when(securityContext.getAuthentication()).thenReturn(authentication);

            SecurityContextHolder.setContext(securityContext);
            when(usersRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));

            Users result = authService.getCurrentAuthenticatedUser();

            assertNotNull(result);
            assertEquals("user@test.com", result.getEmail());

            SecurityContextHolder.clearContext(); // Clean up
        }

        @Test
        void getCurrentAuthenticatedUser_ThrowsEntityNotFound_WhenUserMissing() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("missing@test.com");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            when(usersRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> authService.getCurrentAuthenticatedUser());
            SecurityContextHolder.clearContext();
        }
    }
}