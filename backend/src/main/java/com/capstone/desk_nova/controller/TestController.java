package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.model.Users;
import com.capstone.desk_nova.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/api/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Hello Admin!";
    }

    @GetMapping("/api/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String agentOrAdmin() {
        return "Hello Agent or Admin!";
    }
    @GetMapping("/api/client")
    @PreAuthorize("hasRole('CLIENT')")
    public String client() {
        return "Hello client!";
    }

    @GetMapping("/api/test-user")
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public String anyUser() {
        return "Hello Authenticated User!";
    }

    @GetMapping("/api/me")
    public Users me(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null && auth.isAuthenticated()){
            return this.usersRepository.findByEmail(auth.getName()).orElseThrow(
                    () -> new EntityNotFoundException("User not found")
            );
        }

        throw new SecurityException("Unauthorized");

    }


}