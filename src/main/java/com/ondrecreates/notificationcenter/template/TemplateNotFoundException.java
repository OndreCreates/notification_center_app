package com.ondrecreates.notificationcenter.template;

import com.ondrecreates.notificationcenter.notification.NotificationChannel;

public class TemplateNotFoundException extends RuntimeException {

    public TemplateNotFoundException(String code, NotificationChannel channel) {
        super("Šablona '%s' pro kanál %s nenalezena".formatted(code, channel));
    }
}
