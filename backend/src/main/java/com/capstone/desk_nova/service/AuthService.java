package com.capstone.desk_nova.service;

import com.capstone.desk_nova.dto.auth.LoginRequest;
import com.capstone.desk_nova.dto.auth.RegisterRequest;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.model.enums.Roles;
import com.capstone.desk_nova.repository.UsersRepository;
import com.capstone.desk_nova.security.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;
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

    public Map<String, Object> register(RegisterRequest user) {

        if (usersRepository.findByEmail(user.email()).isPresent()) {
            throw new DuplicateKeyException("Email already exists");
        }

        Users newUser = new Users();
        newUser.setFirstName(user.firstName());
        newUser.setLastName(user.lastName());
        newUser.setRole(Roles.CLIENT);
        newUser.setEmail(user.email());

        newUser.setPassword(passwordEncoder.encode(user.password()));

        Users saved = usersRepository.save(newUser);

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

    public Map<String, Object> login(LoginRequest req) {

        Users users = usersRepository.findByEmail(req.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(req.password(), users.getPassword())) {
            throw new SecurityException("Invalid password");
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