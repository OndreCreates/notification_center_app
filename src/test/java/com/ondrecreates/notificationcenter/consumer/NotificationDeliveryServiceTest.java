package com.ondrecreates.notificationcenter.consumer;

import com.ondrecreates.notificationcenter.client.Client;
import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptRepository;
import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptStatus;
import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import com.ondrecreates.notificationcenter.notification.NotificationRepository;
import com.ondrecreates.notificationcenter.notification.NotificationStatus;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Ověřuje retry stavový automat (viz Fáze 2A v PROJECT_NOTES.md):
 * 1 initiální pokus + 3 retry = MAX_ATTEMPTS 4, pak DEAD.
 */
@ExtendWith(MockitoExtension.class)
class NotificationDeliveryServiceTest {

    private static final Long NOTIFICATION_ID = 1L;
    private static final String FROM_ADDRESS = "test@notification-center.local";

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private DeliveryAttemptRepository deliveryAttemptRepository;

    @Mock
    private JavaMailSender mailSender;

    private NotificationDeliveryService service;

    @BeforeEach
    void setUp() {
        service = new NotificationDeliveryService(notificationRepository, deliveryAttemptRepository, mailSender, FROM_ADDRESS);
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
    }

    @Test
    void successfulDelivery_marksNotificationSentAndRecordsSuccessAttempt() {
        Notification notification = sampleNotification();
        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.of(notification));
        when(deliveryAttemptRepository.countByNotificationId(NOTIFICATION_ID)).thenReturn(0L);

        DeliveryResult result = service.deliver(NOTIFICATION_ID);

        assertThat(result.outcome()).isEqualTo(DeliveryOutcome.SENT);
        assertThat(result.attemptNumber()).isEqualTo(1);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
        verify(deliveryAttemptRepository).save(argThat(a ->
                a.getStatus() == DeliveryAttemptStatus.SUCCESS && a.getAttemptNumber() == 1));
    }

    @Test
    void failedDelivery_belowMaxAttempts_staysPendingAndSchedulesRetry() throws Exception {
        Notification notification = sampleNotification();
        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.of(notification));
        // Žádný předchozí pokus zaznamenaný => tohle je pokus číslo 1 (z max. 4).
        when(deliveryAttemptRepository.countByNotificationId(NOTIFICATION_ID)).thenReturn(0L);
        doThrow(new MailSendException("SMTP nedostupné")).when(mailSender).send(any(MimeMessage.class));

        DeliveryResult result = service.deliver(NOTIFICATION_ID);

        assertThat(result.outcome()).isEqualTo(DeliveryOutcome.RETRY_SCHEDULED);
        assertThat(result.attemptNumber()).isEqualTo(1);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
        verify(deliveryAttemptRepository).save(argThat(a -> a.getStatus() == DeliveryAttemptStatus.FAILURE));
    }

    @Test
    void failedDelivery_atMaxAttempts_marksDead() throws Exception {
        Notification notification = sampleNotification();
        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.of(notification));
        // 3 předchozí pokusy už zaznamenané => tohle je pokus číslo 4 = MAX_ATTEMPTS.
        when(deliveryAttemptRepository.countByNotificationId(NOTIFICATION_ID)).thenReturn(3L);
        doThrow(new MailSendException("SMTP nedostupné")).when(mailSender).send(any(MimeMessage.class));

        DeliveryResult result = service.deliver(NOTIFICATION_ID);

        assertThat(result.outcome()).isEqualTo(DeliveryOutcome.DEAD);
        assertThat(result.attemptNumber()).isEqualTo(4);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.DEAD);
    }

    private Notification sampleNotification() {
        Client client = Client.builder().id(1L).name("Test Client").build();
        return Notification.builder()
                .id(NOTIFICATION_ID)
                .client(client)
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Předmět")
                .body("Obsah")
                .status(NotificationStatus.PENDING)
                .build();
    }

}
