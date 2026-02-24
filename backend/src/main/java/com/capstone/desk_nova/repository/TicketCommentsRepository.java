package com.capstone.desk_nova.repository;

import com.capstone.desk_nova.model.TicketComments;
import com.capstone.desk_nova.model.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketCommentsRepository extends JpaRepository<TicketComments, Long>{

    List<TicketComments> getByTicketId(Long ticketId);
    List<TicketComments> findByUserId(Long userId);
    List<TicketComments> findByTicketId(Long ticketId);
    List<TicketComments> findByTicketIdOrderByCreatedAtAsc(Long ticketId);

}
