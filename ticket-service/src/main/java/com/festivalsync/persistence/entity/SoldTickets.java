package com.festivalsync.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sold_tickets")
public class SoldTickets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Tickets ticket;

     */

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buyer_email")
    private String buyerEmail;

    @Column(name = "buyer_phone")
    private String buyerPhone;

    @Column(name = "purchase_date", updatable = false)
    private LocalDateTime purchaseDate;

    private String state;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "insert_timestamp", updatable = false)
    private LocalDateTime insertTimestamp;

    @Column(name = "update_timestamp")
    private LocalDateTime updateTimestamp;
}

