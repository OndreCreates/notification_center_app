package com.ondrecreates.notificationcenter.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record CreateNotificationRequest(

        @NotNull(message = "channel je povinné pole")
        NotificationChannel channel,

        @NotBlank(message = "recipient je povinné pole")
        @Email(message = "recipient musí být platná e-mailová adresa")
        String recipient,

        @Size(max = 500, message = "subject může mít maximálně 500 znaků")
        String subject,

        // Právě jedno z body / templateCode musí být vyplněné (ověřeno v NotificationService).
        String body,

        String templateCode,

        Map<String, Object> templateData
) {
}
