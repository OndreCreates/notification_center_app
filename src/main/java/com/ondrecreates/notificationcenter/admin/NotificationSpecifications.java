package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import com.ondrecreates.notificationcenter.notification.NotificationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

final class NotificationSpecifications {

    private NotificationSpecifications() {
    }

    static Specification<Notification> hasStatus(NotificationStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    static Specification<Notification> hasChannel(NotificationChannel channel) {
        return (root, query, cb) -> channel == null ? null : cb.equal(root.get("channel"), channel);
    }

    static Specification<Notification> hasClientId(Long clientId) {
        return (root, query, cb) -> clientId == null ? null : cb.equal(root.get("client").get("id"), clientId);
    }

    static Specification<Notification> createdFrom(Instant from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    static Specification<Notification> createdTo(Instant to) {
        return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }
}
