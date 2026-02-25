package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.dto.ticket.TicketMetric;
import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.service.MetricService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metric")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class MetricController {

    private final MetricService metricService;

    @GetMapping("/ticket")
    public ResponseEntity<List<Tickets>> getAllTickets() {
        return ResponseEntity.ok(metricService.getAllTickets());
    }

    @GetMapping("/admin")
    public ResponseEntity<TicketMetric> getAdminMetrics() {
        return ResponseEntity.ok(metricService.getAdminMetrics());
    }


}
