package com.ondrecreates.notificationcenter.channel;

import com.ondrecreates.notificationcenter.delivery.DeliveryAttempt;
import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptRepository;
import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptStatus;
import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import com.ondrecreates.notificationcenter.notification.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * In-app kanál je synchronní a bez retry – živý push do STOMP brokeru buď
 * projde hned (klient teď posílá zprávu do fronty pro připojené odběratele),
 * nebo ne. Na rozdíl od EMAIL kanálu nejde přes RabbitMQ, protože opožděný
 * "retry" o 5s/30s/2min pro live notifikaci nedává smysl – buď je klient
 * připojený teď, nebo zprávu propásne.
 */
@Slf4j
@Component
public class WebSocketNotificationChannelHandler implements NotificationChannelHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final DeliveryAttemptRepository deliveryAttemptRepository;

    public WebSocketNotificationChannelHandler(SimpMessagingTemplate messagingTemplate,
                                                DeliveryAttemptRepository deliveryAttemptRepository) {
        this.messagingTemplate = messagingTemplate;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.WEBSOCKET;
    }

    @Override
    @Transactional
    public void dispatch(Notification notification) {
        try {
            InAppNotificationPayload payload = new InAppNotificationPayload(
                    notification.getId(), notification.getSubject(), notification.getBody());

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + notification.getClient().getId(), payload);

            recordAttempt(notification, DeliveryAttemptStatus.SUCCESS, null);
            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            log.warn("Doručení in-app notifikace {} selhalo: {}", notification.getId(), e.getMessage());
            recordAttempt(notification, DeliveryAttemptStatus.FAILURE, e.getMessage());
            notification.setStatus(NotificationStatus.DEAD);
        }
    }

    private void recordAttempt(Notification notification, DeliveryAttemptStatus status, String errorMessage) {
        DeliveryAttempt attempt = DeliveryAttempt.builder()
                .notification(notification)
                .attemptNumber(1)
                .status(status)
                .errorMessage(errorMessage)
                .build();
        deliveryAttemptRepository.save(attempt);
    }
}
