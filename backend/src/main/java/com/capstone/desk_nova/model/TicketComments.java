package com.capstone.desk_nova.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name ="ticket_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="ticket_id")
    private Tickets ticket;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    @Column(nullable = false)
    private String comment;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

}
