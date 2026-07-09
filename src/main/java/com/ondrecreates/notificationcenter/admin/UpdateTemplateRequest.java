package com.ondrecreates.notificationcenter.admin;

import jakarta.validation.constraints.NotBlank;

public record UpdateTemplateRequest(
        @NotBlank(message = "content je povinné pole")
        String content
) {
}
