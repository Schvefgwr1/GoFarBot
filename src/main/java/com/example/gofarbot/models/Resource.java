package com.example.gofarbot.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resources")
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1024)
    private String link;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private ResType type;
}
