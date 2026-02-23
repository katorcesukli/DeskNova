package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.Tickets;
import com.capstone.desk_nova.repository.TicketsRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class TicketsService {

    private final TicketsRepository ticketsRepository;


}
