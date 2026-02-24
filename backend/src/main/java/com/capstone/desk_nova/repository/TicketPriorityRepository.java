package com.capstone.desk_nova.repository;

import com.capstone.desk_nova.model.TicketPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketPriorityRepository extends JpaRepository<TicketPriority, Long>{
    Optional<TicketPriority> findByName(String name);
}
