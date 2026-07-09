package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTemplateRequest(

        @NotBlank(message = "code je povinné pole")
        String code,

        @NotNull(message = "channel je povinné pole")
        NotificationChannel channel,

        @NotBlank(message = "content je povinné pole")
        String content
) {
}
