package com.example.gofarbot.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "conference")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<UserRegistration> registrations;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Notification.class)
    @JoinTable(
            name = "conferences_notifications_rel",
            joinColumns = {@JoinColumn(name = "conference")},
            inverseJoinColumns = {@JoinColumn(name = "notification")}
    )
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Notification> notifications;

    public List<User> getUsers() {
        List<UserRegistration> registrations = this.registrations;
        List<User> users = new ArrayList<>();
        for(UserRegistration registration: registrations) {
            users.add(registration.getUser());
        }
        return users;
    }
}
