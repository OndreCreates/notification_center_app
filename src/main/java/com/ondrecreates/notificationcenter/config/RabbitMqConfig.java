package com.ondrecreates.notificationcenter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String NOTIFICATIONS_EXCHANGE = "notifications.exchange";
    public static final String EMAIL_QUEUE = "notifications.email.queue";
    public static final String EMAIL_ROUTING_KEY = "notification.email";

    // Retry řetěz: TTL + dead-letter-exchange pattern (RabbitMQ nemá nativní delay
    // bez pluginu). Zpráva čeká v retry frontě po dobu TTL, pak ji broker sám
    // dead-letteruje zpátky do hlavní fronty přes výchozí exchange (routing key = název fronty).
    public static final String EMAIL_RETRY_5S_QUEUE = "notifications.email.retry.5s";
    public static final String EMAIL_RETRY_30S_QUEUE = "notifications.email.retry.30s";
    public static final String EMAIL_RETRY_2M_QUEUE = "notifications.email.retry.2m";
    public static final String EMAIL_DLQ = "notifications.email.dlq";

    private static final int RETRY_5S_MS = 5_000;
    private static final int RETRY_30S_MS = 30_000;
    private static final int RETRY_2M_MS = 120_000;

    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange(NOTIFICATIONS_EXCHANGE, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange notificationsExchange) {
        return BindingBuilder.bind(emailQueue).to(notificationsExchange).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Queue emailRetry5sQueue() {
        return retryQueue(EMAIL_RETRY_5S_QUEUE, RETRY_5S_MS);
    }

    @Bean
    public Queue emailRetry30sQueue() {
        return retryQueue(EMAIL_RETRY_30S_QUEUE, RETRY_30S_MS);
    }

    @Bean
    public Queue emailRetry2mQueue() {
        return retryQueue(EMAIL_RETRY_2M_QUEUE, RETRY_2M_MS);
    }

    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(EMAIL_DLQ).build();
    }

    private Queue retryQueue(String name, int ttlMillis) {
        return QueueBuilder.durable(name)
                .withArgument("x-message-ttl", ttlMillis)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", EMAIL_QUEUE)
                .build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
