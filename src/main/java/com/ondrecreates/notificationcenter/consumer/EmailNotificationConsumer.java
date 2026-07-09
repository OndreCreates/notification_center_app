package com.ondrecreates.notificationcenter.consumer;

import com.ondrecreates.notificationcenter.config.RabbitMqConfig;
import com.ondrecreates.notificationcenter.notification.NotificationQueueMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class EmailNotificationConsumer {

    private final NotificationDeliveryService deliveryService;

    public EmailNotificationConsumer(NotificationDeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @RabbitListener(queues = RabbitMqConfig.EMAIL_QUEUE)
    public void handle(NotificationQueueMessage message, Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            deliveryService.deliver(message.notificationId());
        } catch (Exception e) {
            log.error("Neočekávaná chyba při zpracování notifikace {}: {}", message.notificationId(), e.getMessage(), e);
        } finally {
            channel.basicAck(deliveryTag, false);
        }
    }
}
