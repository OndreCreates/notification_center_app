package com.ondrecreates.notificationcenter.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminSecurityConfig {

    @Bean
    public FilterRegistrationBean<AdminApiKeyFilter> adminApiKeyFilter(
            @Value("${app.admin.api-key}") String adminApiKey) {
        FilterRegistrationBean<AdminApiKeyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AdminApiKeyFilter(adminApiKey));
        registration.addUrlPatterns("/api/v1/admin/*");
        registration.setOrder(1);
        return registration;
    }
}
