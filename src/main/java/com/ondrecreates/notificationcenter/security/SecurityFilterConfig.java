package com.ondrecreates.notificationcenter.security;

import com.ondrecreates.notificationcenter.client.ClientRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityFilterConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyAuthFilter(ClientRepository clientRepository) {
        FilterRegistrationBean<ApiKeyAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ApiKeyAuthFilter(clientRepository));
        registration.addUrlPatterns("/api/v1/*");
        registration.setOrder(1);
        return registration;
    }
}
