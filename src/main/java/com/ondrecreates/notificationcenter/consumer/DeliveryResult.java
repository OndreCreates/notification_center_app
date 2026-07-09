package com.ondrecreates.notificationcenter.consumer;

public record DeliveryResult(DeliveryOutcome outcome, int attemptNumber) {
}
