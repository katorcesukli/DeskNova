package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.TicketComments;
import com.capstone.desk_nova.repository.TicketCommentsRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class TicketCommentsService {

    private final TicketCommentsRepository ticketCommentsRepository;


}
