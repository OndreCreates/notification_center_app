package com.ondrecreates.notificationcenter.consumer;

import com.ondrecreates.notificationcenter.config.RabbitMqConfig;
import com.ondrecreates.notificationcenter.notification.NotificationQueueMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class EmailNotificationConsumer {

    private final NotificationDeliveryService deliveryService;
    private final RabbitTemplate rabbitTemplate;

    public EmailNotificationConsumer(NotificationDeliveryService deliveryService, RabbitTemplate rabbitTemplate) {
        this.deliveryService = deliveryService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMqConfig.EMAIL_QUEUE)
    public void handle(NotificationQueueMessage message, Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            DeliveryResult result = deliveryService.deliver(message.notificationId());
            switch (result.outcome()) {
                case SENT -> log.info("Notifikace {} úspěšně doručena.", message.notificationId());
                case RETRY_SCHEDULED -> scheduleRetry(message, result.attemptNumber());
                case DEAD -> sendToDlq(message);
            }
        } catch (Exception e) {
            log.error("Neočekávaná chyba při zpracování notifikace {}: {}", message.notificationId(), e.getMessage(), e);
        } finally {
            channel.basicAck(deliveryTag, false);
        }
    }

    private void scheduleRetry(NotificationQueueMessage message, int failedAttemptNumber) {
        String retryQueue = switch (failedAttemptNumber) {
            case 1 -> RabbitMqConfig.EMAIL_RETRY_5S_QUEUE;
            case 2 -> RabbitMqConfig.EMAIL_RETRY_30S_QUEUE;
            case 3 -> RabbitMqConfig.EMAIL_RETRY_2M_QUEUE;
            default -> throw new IllegalStateException("Neočekávané číslo pokusu pro retry: " + failedAttemptNumber);
        };

        log.info("Notifikace {} jde na retry (pokus {} selhal) do fronty {}.",
                message.notificationId(), failedAttemptNumber, retryQueue);
        rabbitTemplate.convertAndSend("", retryQueue, message, RabbitMqConfig.persistent());
    }

    private void sendToDlq(NotificationQueueMessage message) {
        log.warn("Notifikace {} vyčerpala všechny pokusy, jde do DLQ.", message.notificationId());
        rabbitTemplate.convertAndSend("", RabbitMqConfig.EMAIL_DLQ, message, RabbitMqConfig.persistent());
    }
}
