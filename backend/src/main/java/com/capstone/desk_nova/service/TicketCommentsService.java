package com.capstone.desk_nova.service;

import com.capstone.desk_nova.model.TicketComments;
import com.capstone.desk_nova.repository.TicketCommentsRepository;
import com.capstone.desk_nova.repository.UsersRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class TicketCommentsService {

    private final TicketCommentsRepository ticketCommentsRepository;
    private final UsersRepository usersRepository;

    public List<TicketComments> getCommentsByTicket(Long ticketId) {
        return ticketCommentsRepository.findByTicketId(ticketId);
    }

    public TicketComments addComment(TicketComments comment) {
        return ticketCommentsRepository.save(comment);
    }

    public void deleteComment(Long id) {
        ticketCommentsRepository.deleteById(id);
    }

}
