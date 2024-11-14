package com.example.gofarbot.services.notifications;


import com.example.gofarbot.data.ConferenceRepository;
import com.example.gofarbot.models.Conference;
import com.example.gofarbot.models.Notification;
import com.example.gofarbot.models.NotificationType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class DynamicNotificationService {

    private final NotificationService notificationService;
    private final ConferenceRepository conferenceRepository;
    private final MessageNotificationService messageNotificationService;

    public void scheduleNotificationForConference(LocalDateTime conferenceTime) {

        Optional<Conference> conferenceInTime = conferenceRepository.findConferenceByTime(conferenceTime);
        if(conferenceInTime.isPresent()) {
            List<Notification> notifications = conferenceInTime.get().getNotifications();
            for(Notification notification: notifications) {
                if(notification.getType() != null &&
                   notification.getType().getType() == NotificationType.NotificationTypes.IN_HANDLE_TIME
                ) {
                    if(notification.getTime() != null) {
                        long delayHandle = LocalDateTime.now().until(notification.getTime(), java.time.temporal.ChronoUnit.MILLIS);
                        if(delayHandle > 0) {
                            Runnable task = () -> {
                                Optional<Conference> confContainer = conferenceRepository
                                        .findConferenceByTime(conferenceTime);
                                if(confContainer.isPresent()) {
                                    Conference conference = confContainer.get();
                                    messageNotificationService.sendHandleConferenceNotification(
                                            conference,
                                            notification.getTime()
                                    );
                                }
                                else {
                                    log.warn("(IN_HANDLE_TIME) Incorrect planing to conference in time: {}",
                                            conferenceTime
                                    );
                                }
                            };
                            notificationService.scheduleTask(task, delayHandle, TimeUnit.MILLISECONDS);
                        }
                    }
                }
            }
        }

        long delayADay = LocalDateTime.now().until(conferenceTime.minusDays(1), java.time.temporal.ChronoUnit.MILLIS);
        List<Conference> conferences = conferenceRepository.findAllByDate(conferenceTime.minusDays(1).toLocalDate());
        if (delayADay > 0 && conferences.isEmpty()) {
            Runnable task = () -> {
                Optional<Conference> confContainer = conferenceRepository
                        .findConferenceByTime(LocalDateTime.now().plusDays(1));
                if(confContainer.isPresent()) {
                    Conference conference = confContainer.get();
                    messageNotificationService.sendPlaningConferenceNotification(
                            conference,
                            NotificationType.NotificationTypes.BEFORE_DAY
                    );
                }
                else {
                    log.warn("(BEFORE_DAY) Incorrect planing to conference in time: {}",
                            LocalDateTime.now().plusDays(1)
                    );
                }
            };
            notificationService.scheduleTask(task, delayADay, TimeUnit.MILLISECONDS);
        }

        long delayTwelve = LocalDateTime.now()
                .until(conferenceTime.toLocalDate().atTime(12, 0), java.time.temporal.ChronoUnit.MILLIS);
        if (delayTwelve > 0) {
            Runnable task = () -> {
                Optional<Conference> confContainer = conferenceRepository
                        .findAllByDate(LocalDate.now()).stream().findFirst();
                if(confContainer.isPresent()) {
                    Conference conference = confContainer.get();
                    if(conference.getTimeOfConference().getHour() > 12) {
                        messageNotificationService.sendPlaningConferenceNotification(
                                conference,
                                NotificationType.NotificationTypes.TWELVE_O_CLOCK
                        );
                    }
                }
                else {
                    log.warn("(TWELVE_O_CLOCK) Incorrect planing to conference in time: {}", LocalDateTime.now());
                }
            };
            notificationService.scheduleTask(task, delayTwelve, TimeUnit.MILLISECONDS);
        }

        long delayAHour = LocalDateTime.now()
                .until(conferenceTime.minusHours(1), java.time.temporal.ChronoUnit.MILLIS);
        if (delayAHour > 0 && conferenceTime.minusHours(1).getHour() != 12) {
            Runnable task = () -> {
                Optional<Conference> confContainer = conferenceRepository
                        .findConferenceByTime(LocalDateTime.now().plusHours(1));
                if(confContainer.isPresent()) {
                    Conference conference = confContainer.get();
                    messageNotificationService.sendPlaningConferenceNotification(
                            conference,
                            NotificationType.NotificationTypes.BEFORE_HOUR
                    );
                }
                else {
                    log.warn("(BEFORE_HOUR) Incorrect planing to conference in time: {}",
                            LocalDateTime.now().plusHours(1)
                    );
                }
            };
            notificationService.scheduleTask(task, delayAHour, TimeUnit.MILLISECONDS);
        }

        long delayLink = LocalDateTime.now()
                .until(conferenceTime.minusMinutes(5), java.time.temporal.ChronoUnit.MILLIS);
        if (delayLink > 0) {
            Runnable task = () -> {
                Optional<Conference> confContainer = conferenceRepository
                        .findConferenceByTime(LocalDateTime.now().plusMinutes(5));
                if(confContainer.isPresent()) {
                    Conference conference = confContainer.get();
                    messageNotificationService.sendPlaningConferenceNotification(
                            conference,
                            NotificationType.NotificationTypes.IN_TIME
                    );
                }
                else {
                    log.warn("(IN_TIME) Incorrect planing to conference in time: {}", LocalDateTime.now());
                }
            };
            notificationService.scheduleTask(task, delayLink, TimeUnit.MILLISECONDS);
        }

        long delayAfterOneAndHalfHour = LocalDateTime.now()
                .until(conferenceTime.plusHours(1).plusMinutes(30), java.time.temporal.ChronoUnit.MILLIS);
        if (delayAfterOneAndHalfHour > 0) {
            Runnable task = () -> {
                Optional<Conference> confContainer = conferenceRepository
                        .findConferenceByTime(LocalDateTime.now().minusHours(1).minusMinutes(30));
                if(confContainer.isPresent()) {
                    Conference conference = confContainer.get();
                    messageNotificationService.sendPlaningConferenceNotification(
                            conference,
                            NotificationType.NotificationTypes.AFTER_ONE_AND_HALF_OUR
                    );
                }
                else {
                    log.warn("(AFTER_ONE_AND_HALF_OUR) Incorrect planing to conference in time: {}",
                            LocalDateTime.now().minusHours(1).minusMinutes(30)
                    );
                }
            };
            notificationService.scheduleTask(task, delayAfterOneAndHalfHour, TimeUnit.MILLISECONDS);
        }

        long delayAfterTwoHours = LocalDateTime.now()
                .until(conferenceTime.plusHours(2),  java.time.temporal.ChronoUnit.MILLIS);
        if (delayAfterTwoHours > 0) {
            Runnable task = () -> {
                Optional<Conference> confContainer = conferenceRepository
                        .findConferenceByTime(LocalDateTime.now().minusHours(2));
                if(confContainer.isPresent()) {
                    Conference conference = confContainer.get();
                    messageNotificationService.sendPlaningConferenceNotification(
                            conference,
                            NotificationType.NotificationTypes.AFTER_TWO_HOURS
                    );
                }
                else {
                    log.warn("(AFTER_TWO_HOURS) Incorrect planing to conference in time: {}",
                            LocalDateTime.now().minusHours(2)
                    );
                }
            };
            notificationService.scheduleTask(task, delayAfterTwoHours, TimeUnit.MILLISECONDS);
        }
    }
}
