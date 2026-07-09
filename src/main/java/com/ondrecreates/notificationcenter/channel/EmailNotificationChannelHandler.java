package com.ondrecreates.notificationcenter.channel;

import com.ondrecreates.notificationcenter.config.RabbitMqConfig;
import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import com.ondrecreates.notificationcenter.notification.NotificationQueueMessage;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationChannelHandler implements NotificationChannelHandler {

    private final RabbitTemplate rabbitTemplate;

    public EmailNotificationChannelHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void dispatch(Notification notification) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.NOTIFICATIONS_EXCHANGE,
                RabbitMqConfig.EMAIL_ROUTING_KEY,
                new NotificationQueueMessage(notification.getId()),
                message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                }
        );
    }
}
