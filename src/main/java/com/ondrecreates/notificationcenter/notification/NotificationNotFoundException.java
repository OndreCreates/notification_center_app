package com.ondrecreates.notificationcenter.notification;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(Long id) {
        super("Notifikace %d nenalezena".formatted(id));
    }
}
