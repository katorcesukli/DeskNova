package com.capstone.desk_nova.repository;

import com.capstone.desk_nova.dto.ticket.AgentWorkloadResponse;
import com.capstone.desk_nova.model.Tickets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long>{

    Page<Tickets> findByClient_Id(Long clientId,  Pageable pageable);

    Page<Tickets> findByAgent_Id(Long agentId, Pageable pageable);

    Optional<Tickets> findByIdAndAgent_Id(Long id, Long agentId);

    Optional<Tickets> findByIdAndClient_Id(Long id, Long agentId);

    boolean existsById(Long id);

    @Query("""
        SELECT new com.capstone.desk_nova.dto.ticket.AgentWorkloadResponse(
            t.agent.id,
            SUM(t.priority.weight)
        )
        FROM Tickets t
        WHERE t.status IN ('OPEN','IN_PROGRESS')
          AND t.agent IS NOT NULL
        GROUP BY t.agent.id
        ORDER BY SUM(t.priority.weight) ASC
    """)
    List<AgentWorkloadResponse> getAgentWorkloads();
}
