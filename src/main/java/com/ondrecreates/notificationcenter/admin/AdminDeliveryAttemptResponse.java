package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.delivery.DeliveryAttempt;
import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptStatus;

import java.time.Instant;

public record AdminDeliveryAttemptResponse(
        int attemptNumber,
        DeliveryAttemptStatus status,
        String errorMessage,
        Instant attemptedAt
) {

    static AdminDeliveryAttemptResponse from(DeliveryAttempt attempt) {
        return new AdminDeliveryAttemptResponse(
                attempt.getAttemptNumber(),
                attempt.getStatus(),
                attempt.getErrorMessage(),
                attempt.getAttemptedAt()
        );
    }
}
