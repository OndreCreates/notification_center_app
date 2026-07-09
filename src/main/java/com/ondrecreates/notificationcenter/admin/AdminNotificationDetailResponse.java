package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import com.ondrecreates.notificationcenter.notification.NotificationStatus;

import java.time.Instant;
import java.util.List;

public record AdminNotificationDetailResponse(
        Long id,
        Long clientId,
        String clientName,
        NotificationChannel channel,
        String recipient,
        String subject,
        String body,
        NotificationStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<AdminDeliveryAttemptResponse> attempts
) {

    static AdminNotificationDetailResponse from(Notification notification, List<AdminDeliveryAttemptResponse> attempts) {
        return new AdminNotificationDetailResponse(
                notification.getId(),
                notification.getClient().getId(),
                notification.getClient().getName(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getBody(),
                notification.getStatus(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                attempts
        );
    }
}
