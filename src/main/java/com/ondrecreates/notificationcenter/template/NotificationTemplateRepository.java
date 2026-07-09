package com.ondrecreates.notificationcenter.template;

import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByCodeAndChannel(String code, NotificationChannel channel);
}
