package com.capstone.desk_nova.controller;


import com.capstone.desk_nova.dto.auth.AuthResponse;
import com.capstone.desk_nova.dto.auth.LoginRequest;
import com.capstone.desk_nova.dto.auth.RegisterRequest;

import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest user
    ) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<Users> me() {
        return ResponseEntity.ok(authService.getCurrentAuthenticatedUser());
    }
}