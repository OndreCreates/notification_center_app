package com.ondrecreates.notificationcenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Configuration
public class ThymeleafConfig {

    /**
     * Obsah šablon žije v DB (NotificationTemplate.content), ne v souborech pod
     * classpath:/templates/ – proto vlastní engine se StringTemplateResolver
     * místo Spring Boot autoconfigurovaného file-based resolveru.
     * SpringTemplateEngine (ne "holý" TemplateEngine) používá SpringEL místo
     * OGNL pro vyhodnocení výrazů (${...}) – OGNL by vyžadovalo extra závislost.
     */
    @Bean
    public TemplateEngine stringTemplateEngine() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }
}
