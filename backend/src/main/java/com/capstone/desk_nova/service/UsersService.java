package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.repository.UsersRepository;
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

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public Users createUsers(Users users) {
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

        return savedUser;
    }
    //ADMIN CRUD METHODS BLOCK
    @Transactional
    public Users updateUser(String email, Users updateDetails) {
        return usersRepository.findByEmail(email).map(user -> {
            user.setEmail(updateDetails.getEmail());
            user.setFirstName(updateDetails.getFirstName());
            user.setLastName(updateDetails.getLastName());
            user.setRole(updateDetails.getRole());
            user.setUpdatedAt(java.time.LocalDateTime.now());

            // Only update password if a new one is provided
            if (updateDetails.getPassword() != null && !updateDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateDetails.getPassword()));
            }

            return usersRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
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
