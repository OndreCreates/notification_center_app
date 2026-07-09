package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import com.ondrecreates.notificationcenter.notification.NotificationStatus;

import java.time.Instant;

public record AdminNotificationSummaryResponse(
        Long id,
        String clientName,
        NotificationChannel channel,
        String recipient,
        String subject,
        NotificationStatus status,
        Instant createdAt
) {

    static AdminNotificationSummaryResponse from(Notification notification) {
        return new AdminNotificationSummaryResponse(
                notification.getId(),
                notification.getClient().getName(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }
}
