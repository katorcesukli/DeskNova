package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.Roles;
import com.capstone.desk_nova.repository.UsersRepository;
import com.capstone.desk_nova.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsersRepository usersRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {

        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> register(Users users) {

        if (usersRepository.findByEmail(users.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setRole(Roles.CLIENT);

        Users saved = usersRepository.save(users);

        String token = jwtUtil.generateToken(
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail(),
                saved.getRole().name()
        );


        return Map.of(
                "token", token,
                "userId", saved.getId(),
                "firstName", saved.getFirstName(),
                "lastName", saved.getLastName(),
                "email", saved.getEmail(),
                "role", saved.getRole()
        );
    }

    public Map<String, Object> login(String email, String password) {

        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!passwordEncoder.matches(password, users.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(
                users.getFirstName(),
                users.getLastName(),
                users.getEmail(),
                users.getRole().name()
        );

        return Map.of(
                "token", token,
                "userId", users.getId(),
                "firstName", users.getFirstName(),
                "lastName", users.getLastName(),
                "email", users.getEmail(),
                "role", users.getRole()
        );
    }
}