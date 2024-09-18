package com.example.gofarbot.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_types")
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationTypes type;

    public enum NotificationTypes {
        BEFORE_DAY, TWELVE_O_CLOCK, BEFORE_HOUR, IN_TIME, AFTER_ONE_AND_HALF_OUR, AFTER_TWO_HOURS
    }
}
