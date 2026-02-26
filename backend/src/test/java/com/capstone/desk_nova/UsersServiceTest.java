package com.capstone.desk_nova;

import com.capstone.desk_nova.dto.person.UserResponse;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.Roles;
import com.capstone.desk_nova.repository.UsersRepository;
import com.capstone.desk_nova.service.EmailService;
import com.capstone.desk_nova.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

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
        testUser.setRole(Roles.CLIENT);
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should save user and encode password successfully")
        void createUser_Success() {
            // Arrange
            when(usersRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
            when(usersRepository.save(any(Users.class))).thenReturn(testUser);

            // Act
            UserResponse savedUser = usersService.createUsers(testUser);

            // Assert
            assertNotNull(savedUser);
            assertEquals("encodedPassword", testUser.getPassword());
            verify(usersRepository, times(1)).save(testUser);
            verify(emailService, times(1)).sendWelcomeEmail(any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void createUser_EmailExists_ThrowsException() {
            // Arrange
            when(usersRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                usersService.createUsers(testUser);
            });

            assertTrue(exception.getMessage().contains("Email already exists"));
            verify(usersRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user details and encode new password")
        void updateUser_Success() {
            Users updatedDetails = new Users();
            updatedDetails.setEmail("new@google.com");
            updatedDetails.setPassword("Password123");

            // don't change these fields
            updatedDetails.setRole(testUser.getRole());
            updatedDetails.setUpdatedAt(LocalDateTime.now());

            when(usersRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.ofNullable(testUser));
            when(passwordEncoder.encode("Password123")).thenReturn("newEncodedPassword");
            when(usersRepository.save(any(Users.class))).thenReturn(testUser);

            UserResponse result = usersService.updateUser(testUser.getEmail(), updatedDetails);

            assertEquals("new@google.com", result.email());
            assertEquals("newEncodedPassword", result.password());
            verify(usersRepository).save(testUser);
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user when email exists")
        void deleteUser_Success() {
            when(usersRepository.existsByEmail("test@google.com")).thenReturn(true);

            usersService.deleteUser("test@google.com");

            verify(usersRepository, times(1)).deleteByEmail("test@google.com");
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void deleteUser_NotFound_ThrowsException() {
            when(usersRepository.existsByEmail("wrongemail@google.com")).thenReturn(false);

            assertThrows(RuntimeException.class, () -> usersService.deleteUser("wrongemail@google.com"));
        }
    }
}