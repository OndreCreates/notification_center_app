package com.ondrecreates.notificationcenter.template;

import com.ondrecreates.notificationcenter.common.NotFoundException;
import com.ondrecreates.notificationcenter.notification.NotificationChannel;

public class TemplateNotFoundException extends NotFoundException {

    public TemplateNotFoundException(String code, NotificationChannel channel) {
        super("Šablona '%s' pro kanál %s nenalezena".formatted(code, channel));
    }

    public TemplateNotFoundException(Long id) {
        super("Šablona %d nenalezena".formatted(id));
    }
}
