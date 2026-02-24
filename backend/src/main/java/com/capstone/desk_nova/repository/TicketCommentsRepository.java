package com.capstone.desk_nova.repository;

import com.capstone.desk_nova.model.TicketComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketCommentsRepository extends JpaRepository<TicketComments, Long>{
    List<TicketComments> findByTicketId(Long ticketId);
    boolean existsById(Long id);
}
