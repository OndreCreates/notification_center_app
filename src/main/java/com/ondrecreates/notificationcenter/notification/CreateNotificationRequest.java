package com.ondrecreates.notificationcenter.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(

        @NotNull(message = "channel je povinné pole")
        NotificationChannel channel,

        @NotBlank(message = "recipient je povinné pole")
        @Email(message = "recipient musí být platná e-mailová adresa")
        String recipient,

        @Size(max = 500, message = "subject může mít maximálně 500 znaků")
        String subject,

        @NotBlank(message = "body je povinné pole")
        String body
) {
}
