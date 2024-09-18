package com.example.gofarbot.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conferences")
public class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String link;

    @NotNull
    private String name;

    @NotNull
    private LocalDateTime timeOfConference;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinTable(
            name = "conferences_users_rel",
            joinColumns = {@JoinColumn(name = "conference")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private List<User> users;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Notification.class)
    @JoinTable(
            name = "conferences_notifications_rel",
            joinColumns = {@JoinColumn(name = "conference")},
            inverseJoinColumns = {@JoinColumn(name = "notification")}
    )
    private List<Notification> notifications;
}
