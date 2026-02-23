package com.capstone.desk_nova.controller;


import com.capstone.desk_nova.model.Users;

import com.capstone.desk_nova.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Users users) {
        return authService.register(users);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {

        return authService.login(
                body.get("email"),
                body.get("password")
        );
    }
}