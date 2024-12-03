package com.festivalsync.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "artists")
public class Artists {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Events event;

    private String genre;

    private String country;

    private String location;

    private String state;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "insert_timestamp", updatable = false)
    private LocalDateTime insertTimestamp;

    @Column(name = "update_timestamp")
    private LocalDateTime updateTimestamp;
}

