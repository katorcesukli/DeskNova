package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.dto.person.UserResponse;
import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
//@CrossOrigin(originPatterns = "*")
public class UsersController {

    private final UsersService usersService;

    // Create a new user
    // Email test as well
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> registerUser(@RequestBody Users user) {
        return new ResponseEntity<>(usersService.createUsers(user), HttpStatus.CREATED);
    }

    // Get all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    // Search for a user by email
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(usersService.findByEmail(email));
    }

    //ADMIN ENDPOINTS
    //CREATE: Reuse registration logic for Admin-created users
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody Users user) {
        return new ResponseEntity<>(usersService.createUsers(user), HttpStatus.CREATED);
    }


    // UPDATE: Update user details by email
    @PutMapping("/admin/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String email, @RequestBody Users userDetails) {
        return ResponseEntity.ok(usersService.updateUser(email, userDetails));
    }

    // DELETE: Remove a user using email as the identifier
    @DeleteMapping("/admin/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        usersService.deleteUser(email);
        return ResponseEntity.ok("User with email " + email + " deleted successfully.");
    }
}
