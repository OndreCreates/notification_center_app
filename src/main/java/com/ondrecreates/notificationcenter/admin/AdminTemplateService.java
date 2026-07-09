package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.template.NotificationTemplate;
import com.ondrecreates.notificationcenter.template.NotificationTemplateRepository;
import com.ondrecreates.notificationcenter.template.TemplateNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminTemplateService {

    private final NotificationTemplateRepository templateRepository;

    public AdminTemplateService(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Transactional(readOnly = true)
    public List<TemplateResponse> list() {
        return templateRepository.findAll().stream()
                .map(TemplateResponse::from)
                .toList();
    }

    @Transactional
    public TemplateResponse create(CreateTemplateRequest request) {
        NotificationTemplate template = NotificationTemplate.builder()
                .code(request.code())
                .channel(request.channel())
                .content(request.content())
                .build();

        template = templateRepository.save(template);

        return TemplateResponse.from(template);
    }

    @Transactional
    public TemplateResponse update(Long id, UpdateTemplateRequest request) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException(id));

        template.setContent(request.content());

        return TemplateResponse.from(template);
    }

    @Transactional
    public void delete(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new TemplateNotFoundException(id);
        }
        templateRepository.deleteById(id);
    }
}
