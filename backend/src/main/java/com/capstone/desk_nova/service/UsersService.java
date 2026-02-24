package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.repository.UsersRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
