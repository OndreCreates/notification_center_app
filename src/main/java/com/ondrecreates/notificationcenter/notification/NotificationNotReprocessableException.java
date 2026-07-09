package com.ondrecreates.notificationcenter.notification;

public class NotificationNotReprocessableException extends RuntimeException {

    public NotificationNotReprocessableException(Long id, NotificationStatus currentStatus) {
        super("Notifikaci %d lze reprocessovat jen ve stavu DEAD, aktuální stav: %s".formatted(id, currentStatus));
    }
}
