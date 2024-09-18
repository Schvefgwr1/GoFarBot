package com.example.gofarbot.services.notifications;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class ScheduledExecutorService{

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit unit) {
        return scheduler.schedule(task, delay, unit);
    }

    public void cancelTask(ScheduledFuture<?> future) {
        if (future != null) {
            future.cancel(false);
        }
    }
}

