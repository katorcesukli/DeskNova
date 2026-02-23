package com.capstone.desk_nova.repository;

import com.capstone.desk_nova.model.TicketComments;
import com.capstone.desk_nova.model.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long>{

    List<Tickets> findById(Long Id);

    List<Tickets> getById(Long Id);
    List<Tickets> findByStatus(String status);
    List<Tickets> findByClientId(Long clientId);
}
