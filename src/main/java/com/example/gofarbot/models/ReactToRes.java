package com.example.gofarbot.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "react_to_res")
@Data
public class ReactToRes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id")
    private Resource resource;
}

