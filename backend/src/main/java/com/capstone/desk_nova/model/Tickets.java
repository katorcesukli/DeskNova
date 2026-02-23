package com.capstone.desk_nova.model;

import com.capstone.desk_nova.model.enums.TicketCategory;
import com.capstone.desk_nova.model.enums.TicketPriority;
import com.capstone.desk_nova.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name ="tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tickets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Users clientId;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Users agentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    @CreationTimestamp
    @Column(name = "date_opened", nullable = false)
    private LocalDateTime dateOpened;

    @Column(name = "date_closed")
    private LocalDateTime dateClosed;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
