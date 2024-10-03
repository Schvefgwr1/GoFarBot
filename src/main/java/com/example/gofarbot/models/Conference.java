package com.example.gofarbot.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<User> users;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Notification.class)
    @JoinTable(
            name = "conferences_notifications_rel",
            joinColumns = {@JoinColumn(name = "conference")},
            inverseJoinColumns = {@JoinColumn(name = "notification")}
    )
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Notification> notifications;
}
