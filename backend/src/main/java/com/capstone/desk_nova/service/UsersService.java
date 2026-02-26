package com.capstone.desk_nova.service;

import com.capstone.desk_nova.dto.person.UserResponse;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Data
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public List<UserResponse> getAllUsers() {
        return usersRepository.findAll().stream().map(UserResponse::from).toList();
    }

    public UserResponse findByEmail(String email) {
        return UserResponse.from(usersRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User does not exist")));
    }

    public UserResponse createUsers(Users users) {
        if (usersRepository.existsByEmail(users.getEmail())) {
            throw new RuntimeException("Email already exists: " + users.getEmail());
        }

        users.setPassword(passwordEncoder.encode(users.getPassword()));

        Users savedUser = usersRepository.save(users);

        //email test
        try {
            emailService.sendWelcomeEmail(savedUser);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return UserResponse.from(users);
    }
    //ADMIN CRUD METHODS BLOCK
    @Transactional
    public UserResponse updateUser(String email, Users updateDetails) {

        Users currentUser = usersRepository.findByEmail(email).
                orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        currentUser.setEmail(updateDetails.getEmail());
        currentUser.setFirstName(updateDetails.getFirstName());
        currentUser.setLastName(updateDetails.getLastName());
        currentUser.setRole(updateDetails.getRole());
        currentUser.setUpdatedAt(java.time.LocalDateTime.now());

        // Only update password if a new one is provided
        if (updateDetails.getPassword() != null && !updateDetails.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(updateDetails.getPassword()));
        }

        return UserResponse.from(usersRepository.save(currentUser));
    }

    @Transactional
    public void deleteUser(String email) {
        if (!usersRepository.existsByEmail(email)) {
            throw new RuntimeException("Cannot delete. User not found with email: " + email);
        }
        usersRepository.deleteByEmail(email);
    }
    //END OF ADMIN CRUD METHOD BLOCK

    //ADMIN TICKET METRICS AND MANAGEMENT BLOCK


    //END OF ADMIN TICKET METRICS AND MANAGEMENT BLOCK
}
