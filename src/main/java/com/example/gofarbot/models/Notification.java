package com.example.gofarbot.models;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @ManyToOne
    @JoinColumn(name = "file")
    private File file;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "type")
    private NotificationType type;

    @Nullable
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Link.class)
    @JoinTable(
            name = "notifications_links_rel",
            joinColumns = {@JoinColumn(name = "notification")},
            inverseJoinColumns = {@JoinColumn(name = "link")}
    )
    private List<Link> links;
}
