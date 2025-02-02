package com.example.gofarbot.models;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @Nullable
    private LocalDateTime time;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "file")
    private File file;

    @Nullable
    private Long number;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "type")
    private NotificationType type;
}
