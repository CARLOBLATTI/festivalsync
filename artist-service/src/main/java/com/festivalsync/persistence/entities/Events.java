package com.festivalsync.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "events")
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate date;

    private String location;

    private String country;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Artists> artists = new ArrayList<>();

    private String state;

    @Column(name = "artists_number")
    private int artistsNumber;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "insert_timestamp", updatable = false)
    private LocalDateTime insertTimestamp;

    @Column(name = "update_timestamp")
    private LocalDateTime updateTimestamp;
}

