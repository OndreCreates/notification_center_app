package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.delivery.DeliveryAttemptRepository;
import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import com.ondrecreates.notificationcenter.notification.NotificationNotFoundException;
import com.ondrecreates.notificationcenter.notification.NotificationRepository;
import com.ondrecreates.notificationcenter.notification.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminNotificationService {

    private final NotificationRepository notificationRepository;
    private final DeliveryAttemptRepository deliveryAttemptRepository;

    public AdminNotificationService(NotificationRepository notificationRepository,
                                     DeliveryAttemptRepository deliveryAttemptRepository) {
        this.notificationRepository = notificationRepository;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
    }

    public Page<AdminNotificationSummaryResponse> list(NotificationStatus status,
                                                         NotificationChannel channel,
                                                         Long clientId,
                                                         Instant from,
                                                         Instant to,
                                                         Pageable pageable) {
        Specification<Notification> spec = Specification.allOf(
                NotificationSpecifications.hasStatus(status),
                NotificationSpecifications.hasChannel(channel),
                NotificationSpecifications.hasClientId(clientId),
                NotificationSpecifications.createdFrom(from),
                NotificationSpecifications.createdTo(to)
        );

        return notificationRepository.findAll(spec, pageable)
                .map(AdminNotificationSummaryResponse::from);
    }

    public AdminNotificationDetailResponse detail(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        List<AdminDeliveryAttemptResponse> attempts = deliveryAttemptRepository
                .findByNotificationIdOrderByAttemptNumberAsc(notificationId).stream()
                .map(AdminDeliveryAttemptResponse::from)
                .toList();

        return AdminNotificationDetailResponse.from(notification, attempts);
    }
}
