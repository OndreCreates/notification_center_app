package com.ondrecreates.notificationcenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP nad WebSocket místo syrového WS – dává nám standardizovaný
 * pub/sub model (topics/subscriptions), který přesně sedí na "klient se
 * přihlásí k odběru svých notifikací" use-case. Ruční WS by znamenal
 * vymýšlet vlastní zprávový protokol (subscribe/unsubscribe, routing) znovu.
 * Bez SockJS fallbacku – moderní prohlížeče podporují nativní WebSocket,
 * SockJS by jen přidal zbytečnou vrstvu negotiation komplexity.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}
