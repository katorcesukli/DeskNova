package com.capstone.desk_nova.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name ="comments")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class TicketComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CommentId;

    @ManyToOne
    @JoinColumn(name ="ticketId", referencedColumnName = "ticketId")
    @Column(nullable = false)
    private Integer ticketId;

    @ManyToOne
    @JoinColumn(name = "clientId", referencedColumnName = "userId")
    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

}
