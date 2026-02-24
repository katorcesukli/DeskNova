package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UsersController {

    private UsersService usersService;

    // Create a new user
    // Email test as well
    @PostMapping("/register")
    public ResponseEntity<Users> registerUser(@RequestBody Users user) {
        Users createdUser = usersService.createUsers(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    // Search for a user by email
    @GetMapping("/search")
    public ResponseEntity<Users> getUserByEmail(@RequestParam String email) {
        return usersService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
