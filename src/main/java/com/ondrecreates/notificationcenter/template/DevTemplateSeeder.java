package com.ondrecreates.notificationcenter.template;

import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DevTemplateSeeder implements CommandLineRunner {

    private static final String SAMPLE_CODE = "welcome";

    private final NotificationTemplateRepository templateRepository;

    public DevTemplateSeeder(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public void run(String... args) {
        if (templateRepository.findByCodeAndChannel(SAMPLE_CODE, NotificationChannel.EMAIL).isPresent()) {
            return;
        }

        String content = """
                <div>
                  <h1 th:text="'Ahoj, ' + ${name} + '!'">Ahoj!</h1>
                  <p th:text="${message}">Zpráva</p>
                </div>
                """;

        NotificationTemplate template = NotificationTemplate.builder()
                .code(SAMPLE_CODE)
                .channel(NotificationChannel.EMAIL)
                .content(content)
                .build();

        templateRepository.save(template);
        log.info("Vytvořena ukázková šablona '{}' pro kanál EMAIL (placeholdery: name, message).", SAMPLE_CODE);
    }
}
