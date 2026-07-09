package com.ondrecreates.notificationcenter.consumer;

import com.ondrecreates.notificationcenter.delivery.DeliveryAttempt;
import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptRepository;
import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptStatus;
import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationRepository;
import com.ondrecreates.notificationcenter.notification.NotificationStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class NotificationDeliveryService {

    // 1 initiální pokus + 3 retry (5s/30s/2min) = 4 pokusy celkem, pak DLQ.
    private static final int MAX_ATTEMPTS = 4;

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
    public DeliveryResult deliver(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalStateException("Notifikace %d nenalezena".formatted(notificationId)));

        int attemptNumber = deliveryAttemptRepository
                .findByNotificationIdOrderByAttemptNumberAsc(notificationId).size() + 1;

        try {
            sendEmail(notification);
            recordAttempt(notification, attemptNumber, DeliveryAttemptStatus.SUCCESS, null);
            notification.setStatus(NotificationStatus.SENT);
            return new DeliveryResult(DeliveryOutcome.SENT, attemptNumber);
        } catch (Exception e) {
            log.warn("Pokus {} o doručení notifikace {} selhal: {}", attemptNumber, notificationId, e.getMessage());
            recordAttempt(notification, attemptNumber, DeliveryAttemptStatus.FAILURE, e.getMessage());

            if (attemptNumber >= MAX_ATTEMPTS) {
                notification.setStatus(NotificationStatus.DEAD);
                return new DeliveryResult(DeliveryOutcome.DEAD, attemptNumber);
            }

            // Zůstává PENDING – ještě není definitivně mrtvá, čeká na retry.
            return new DeliveryResult(DeliveryOutcome.RETRY_SCHEDULED, attemptNumber);
        }
    }

    private void sendEmail(Notification notification) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
        helper.setFrom(FROM_ADDRESS);
        helper.setTo(notification.getRecipient());
        helper.setSubject(notification.getSubject());
        helper.setText(notification.getBody(), true);
        mailSender.send(mimeMessage);
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
