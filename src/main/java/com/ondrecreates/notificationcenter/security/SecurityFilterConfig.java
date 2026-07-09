package com.ondrecreates.notificationcenter.security;

import com.ondrecreates.notificationcenter.client.ClientRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityFilterConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyAuthFilter(ClientRepository clientRepository,
                                                                       RateLimitingService rateLimitingService) {
        FilterRegistrationBean<ApiKeyAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ApiKeyAuthFilter(clientRepository, rateLimitingService));
        // Ne "/api/v1/*" – to by jako prefix match zahrnulo i /api/v1/admin/*,
        // který má vlastní (oddělenou) autentizaci přes AdminApiKeyFilter.
        registration.addUrlPatterns("/api/v1/notifications/*", "/api/v1/notifications");
        registration.setOrder(1);
        return registration;
    }
}
