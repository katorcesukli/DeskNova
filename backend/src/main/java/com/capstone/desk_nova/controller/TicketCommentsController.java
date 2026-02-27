package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.dto.ticket.TicketCommentRequest;
import com.capstone.desk_nova.service.TicketCommentsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
//@CrossOrigin(origins = "*")
public class TicketCommentsController {

    @Autowired
    private TicketCommentsService ticketCommentsService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public ResponseEntity<Long> addComment(@Valid @RequestBody TicketCommentRequest req) {
        return ResponseEntity.ok(this.ticketCommentsService.addComment(req));
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public ResponseEntity<Long> editComment(
            @PathVariable Long id,
            @Valid @RequestBody TicketCommentRequest req) {
        return ResponseEntity.ok(this.ticketCommentsService.editTaskComment(id, req));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        this.ticketCommentsService.deleteComment(id);
        return ResponseEntity.ok("Successfully deleted comment");
    }
}
