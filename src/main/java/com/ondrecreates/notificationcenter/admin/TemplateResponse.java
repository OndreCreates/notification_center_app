package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import com.ondrecreates.notificationcenter.template.NotificationTemplate;

import java.time.Instant;

public record TemplateResponse(
        Long id,
        String code,
        NotificationChannel channel,
        String content,
        Instant createdAt,
        Instant updatedAt
) {

    static TemplateResponse from(NotificationTemplate template) {
        return new TemplateResponse(
                template.getId(),
                template.getCode(),
                template.getChannel(),
                template.getContent(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
}
