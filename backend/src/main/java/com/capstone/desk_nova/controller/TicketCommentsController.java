package com.capstone.desk_nova.controller;

import com.capstone.desk_nova.service.TicketCommentsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class TicketCommentsController {

    private TicketCommentsService ticketCommentsService;

}
