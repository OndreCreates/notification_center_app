package com.ondrecreates.notificationcenter.notification;

import com.ondrecreates.notificationcenter.common.NotFoundException;

public class NotificationNotFoundException extends NotFoundException {

    public NotificationNotFoundException(Long id) {
        super("Notifikace %d nenalezena".formatted(id));
    }
}
