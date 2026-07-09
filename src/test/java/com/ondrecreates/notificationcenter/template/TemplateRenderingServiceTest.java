package com.ondrecreates.notificationcenter.template;

import com.ondrecreates.notificationcenter.notification.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateRenderingServiceTest {

    @Mock
    private NotificationTemplateRepository templateRepository;

    private TemplateRenderingService service;

    @BeforeEach
    void setUp() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);

        TemplateEngine templateEngine = engine;
        service = new TemplateRenderingService(templateRepository, templateEngine);
    }

    @Test
    void rendersPlaceholdersFromTemplateContent() {
        NotificationTemplate template = NotificationTemplate.builder()
                .code("welcome")
                .channel(NotificationChannel.EMAIL)
                .content("<div><h1 th:text=\"'Ahoj, ' + ${name} + '!'\">x</h1></div>")
                .build();
        when(templateRepository.findByCodeAndChannel("welcome", NotificationChannel.EMAIL))
                .thenReturn(Optional.of(template));

        String rendered = service.render("welcome", NotificationChannel.EMAIL, Map.of("name", "Ondra"));

        assertThat(rendered).contains("Ahoj, Ondra!");
    }

    @Test
    void throwsWhenTemplateDoesNotExist() {
        when(templateRepository.findByCodeAndChannel("missing", NotificationChannel.EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.render("missing", NotificationChannel.EMAIL, Map.of()))
                .isInstanceOf(TemplateNotFoundException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void rendersWithEmptyDataWhenNoneProvided() {
        NotificationTemplate template = NotificationTemplate.builder()
                .code("static")
                .channel(NotificationChannel.EMAIL)
                .content("<p>Statický obsah bez placeholderů</p>")
                .build();
        when(templateRepository.findByCodeAndChannel("static", NotificationChannel.EMAIL))
                .thenReturn(Optional.of(template));

        String rendered = service.render("static", NotificationChannel.EMAIL, null);

        assertThat(rendered).contains("Statický obsah bez placeholderů");
    }
}
