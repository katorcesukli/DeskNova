package com.capstone.desk_nova.controller;

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
    public ResponseEntity<Users> registerUser(@RequestBody Users user) {
        Users createdUser = usersService.createUsers(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Get all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    // Search for a user by email
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> getUserByEmail(@RequestParam String email) {
        return usersService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    //ADMIN ENDPOINTS
    //CREATE: Reuse registration logic for Admin-created users
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users createdUser = usersService.createUsers(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


    // UPDATE: Update user details by email
    @PutMapping("/admin/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> updateUser(@PathVariable String email, @RequestBody Users userDetails) {
        Users updatedUser = usersService.updateUser(email, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // DELETE: Remove a user using email as the identifier
    @DeleteMapping("/admin/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        usersService.deleteUser(email);
        return ResponseEntity.ok("User with email " + email + " deleted successfully.");
    }
}
