package com.ondrecreates.notificationcenter.template;

import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class TemplateRenderingService {

    private final NotificationTemplateRepository templateRepository;
    private final TemplateEngine templateEngine;

    public TemplateRenderingService(NotificationTemplateRepository templateRepository,
                                     @Qualifier("stringTemplateEngine") TemplateEngine templateEngine) {
        this.templateRepository = templateRepository;
        this.templateEngine = templateEngine;
    }

    public String render(String code, NotificationChannel channel, Map<String, Object> data) {
        NotificationTemplate template = templateRepository.findByCodeAndChannel(code, channel)
                .orElseThrow(() -> new TemplateNotFoundException(code, channel));

        Context context = new Context();
        context.setVariables(data != null ? data : Map.of());

        return templateEngine.process(template.getContent(), context);
    }
}
