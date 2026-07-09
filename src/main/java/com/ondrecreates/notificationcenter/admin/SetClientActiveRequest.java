package com.ondrecreates.notificationcenter.admin;

import jakarta.validation.constraints.NotNull;

public record SetClientActiveRequest(@NotNull Boolean active) {
}
