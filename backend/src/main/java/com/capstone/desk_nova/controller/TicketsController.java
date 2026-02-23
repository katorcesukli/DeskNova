package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.service.TicketsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin
public class TicketsController {

    private TicketsService ticketsService;

    @GetMapping
    public List<Tickets> getAllTickets() {
        return ticketsService.getAllTickets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tickets> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok((Tickets) ticketsService.getTicketsById(id));
    }

    @PostMapping
    public Tickets createTicket(@RequestBody Tickets ticket) {
        return ticketsService.createTicket(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tickets> updateTicket(@PathVariable Long id, @RequestBody Tickets ticketDetails) {
        return ResponseEntity.ok(ticketsService.updateTicket(id, ticketDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketsService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
