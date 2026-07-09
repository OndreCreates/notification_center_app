package com.ondrecreates.notificationcenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Next.js admin panel a demo klient (Fáze 3) běží na jiném portu (localhost:3000)
 * než backend (8080) – bez CORS by prohlížeč fetch/WebSocket handshake odmítl.
 * Povoleno jen pro localhost dev porty, ne "*" – i v MVP zbytečně neotvírat CORS napořád.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**")
                .allowedOriginPatterns("http://localhost:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
