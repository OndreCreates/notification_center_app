package com.ondrecreates.notificationcenter.notification;

import com.ondrecreates.notificationcenter.client.Client;
import com.ondrecreates.notificationcenter.config.RabbitMqConfig;
import com.ondrecreates.notificationcenter.template.TemplateRenderingService;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TemplateRenderingService templateRenderingService;

    public NotificationService(NotificationRepository notificationRepository,
                                RabbitTemplate rabbitTemplate,
                                TemplateRenderingService templateRenderingService) {
        this.notificationRepository = notificationRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.templateRenderingService = templateRenderingService;
    }

    @Transactional
    public Notification createAndPublish(Client client, CreateNotificationRequest request) {
        Notification notification = Notification.builder()
                .client(client)
                .channel(request.channel())
                .recipient(request.recipient())
                .subject(request.subject())
                .body(resolveBody(request))
                .status(NotificationStatus.PENDING)
                .build();

        notification = notificationRepository.save(notification);
        publish(notification);

        return notification;
    }

    private String resolveBody(CreateNotificationRequest request) {
        boolean hasBody = request.body() != null && !request.body().isBlank();
        boolean hasTemplate = request.templateCode() != null && !request.templateCode().isBlank();

        if (hasBody == hasTemplate) {
            throw new InvalidNotificationContentException(
                    "Musí být vyplněno právě jedno z polí: body, nebo templateCode.");
        }

        if (hasBody) {
            return request.body();
        }

        return templateRenderingService.render(request.templateCode(), request.channel(), request.templateData());
    }

    @Transactional
    public Notification reprocess(Client client, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getClient().getId().equals(client.getId()))
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (notification.getStatus() != NotificationStatus.DEAD) {
            throw new NotificationNotReprocessableException(notificationId, notification.getStatus());
        }

        notification.setStatus(NotificationStatus.PENDING);
        publish(notification);

        return notification;
    }

    private void publish(Notification notification) {
        String routingKey = switch (notification.getChannel()) {
            case EMAIL -> RabbitMqConfig.EMAIL_ROUTING_KEY;
        };

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.NOTIFICATIONS_EXCHANGE,
                routingKey,
                new NotificationQueueMessage(notification.getId()),
                message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                }
        );
    }
}
