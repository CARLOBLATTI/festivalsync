package com.festivalsync.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "artists")
public class Artists {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "artist_event",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Events> events = new ArrayList<>();

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
