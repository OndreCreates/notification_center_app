package com.ondrecreates.notificationcenter.channel;

public record InAppNotificationPayload(Long notificationId, String subject, String body) {
}
