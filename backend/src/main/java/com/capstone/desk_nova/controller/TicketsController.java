package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.dto.pagination.PaginatedResponse;
import com.capstone.desk_nova.dto.ticket.AgentWorkloadResponse;
import com.capstone.desk_nova.dto.ticket.CreateTicketRequest;
import com.capstone.desk_nova.dto.ticket.EditTicketRequest;
import com.capstone.desk_nova.dto.ticket.TicketResponse;
import com.capstone.desk_nova.service.TicketsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin
public class TicketsController {

    @Autowired
    private TicketsService ticketsService;

    @GetMapping
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PaginatedResponse<TicketResponse>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(this.ticketsService.getAllTickets(PageRequest.of(page, pageSize, Sort.by("dateOpened"))));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('CLIENT', 'AGENT', 'ADMIN')")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketsService.getTicketById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTicket(@Valid @RequestBody CreateTicketRequest ticket) {
        return ResponseEntity.ok(this.ticketsService.createTicket(ticket) + "Successfully assigned & created ticket");
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editTicket(
            @PathVariable Long id,
            @Valid @RequestBody EditTicketRequest req) {
        return ResponseEntity.ok(ticketsService.editTicket(id, req) + " " + "Successfully edited ticket details");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable Long id) {
        ticketsService.deleteTicket(id);
        return ResponseEntity.ok("Successfully deleted ticket");
    }

    @GetMapping("/test")
    public ResponseEntity<List<AgentWorkloadResponse>> getAgentWorkload() {
        return ResponseEntity.ok(this.ticketsService.getAgentWorkload());
    }
}
