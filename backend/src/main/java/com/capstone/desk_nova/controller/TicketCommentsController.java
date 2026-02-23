package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.model.TicketComments;
import com.capstone.desk_nova.service.TicketCommentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class TicketCommentsController {

    private TicketCommentsService ticketCommentsService;

    @GetMapping("/{ticketId}")
    public ResponseEntity<List<TicketComments>> getCommentsByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketCommentsService.getCommentsByTicket(ticketId));
    }

    @PostMapping
    public ResponseEntity<TicketComments> addComment(@RequestBody TicketComments comment) {
        return ResponseEntity.ok(ticketCommentsService.addComment(comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        ticketCommentsService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
