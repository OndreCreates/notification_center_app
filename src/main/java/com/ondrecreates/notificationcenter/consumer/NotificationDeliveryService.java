package com.ondrecreates.notificationcenter.consumer;

import com.ondrecreates.notificationcenter.delivery.DeliveryAttempt;
import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptRepository;
import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptStatus;
import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationRepository;
import com.ondrecreates.notificationcenter.notification.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class NotificationDeliveryService {

    private static final String FROM_ADDRESS = "notifications@notification-center.local";

    private final NotificationRepository notificationRepository;
    private final DeliveryAttemptRepository deliveryAttemptRepository;
    private final JavaMailSender mailSender;

    public NotificationDeliveryService(NotificationRepository notificationRepository,
                                        DeliveryAttemptRepository deliveryAttemptRepository,
                                        JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void deliver(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalStateException("Notifikace %d nenalezena".formatted(notificationId)));

        int attemptNumber = deliveryAttemptRepository
                .findByNotificationIdOrderByAttemptNumberAsc(notificationId).size() + 1;

        try {
            sendEmail(notification);
            recordAttempt(notification, attemptNumber, DeliveryAttemptStatus.SUCCESS, null);
            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            log.warn("Doručení notifikace {} selhalo: {}", notificationId, e.getMessage());
            recordAttempt(notification, attemptNumber, DeliveryAttemptStatus.FAILURE, e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
        }
    }

    private void sendEmail(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_ADDRESS);
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getSubject());
        message.setText(notification.getBody());
        mailSender.send(message);
    }

    private void recordAttempt(Notification notification, int attemptNumber, DeliveryAttemptStatus status, String errorMessage) {
        DeliveryAttempt attempt = DeliveryAttempt.builder()
                .notification(notification)
                .attemptNumber(attemptNumber)
                .status(status)
                .errorMessage(errorMessage)
                .build();
        deliveryAttemptRepository.save(attempt);
    }
}
