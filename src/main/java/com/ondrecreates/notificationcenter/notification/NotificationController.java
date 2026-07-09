package com.ondrecreates.notificationcenter.notification;

import com.ondrecreates.notificationcenter.client.Client;
import com.ondrecreates.notificationcenter.security.ApiKeyAuthFilter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotificationResponse create(
            @RequestAttribute(ApiKeyAuthFilter.CLIENT_ATTRIBUTE) Client client,
            @Valid @RequestBody CreateNotificationRequest request) {

        Notification notification = notificationService.createAndPublish(client, request);
        return new NotificationResponse(notification.getId(), notification.getStatus());
    }
}
