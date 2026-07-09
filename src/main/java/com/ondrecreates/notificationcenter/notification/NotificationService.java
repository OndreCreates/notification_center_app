package com.ondrecreates.notificationcenter.notification;

import com.ondrecreates.notificationcenter.channel.NotificationChannelHandler;
import com.ondrecreates.notificationcenter.client.Client;
import com.ondrecreates.notificationcenter.template.TemplateRenderingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final TemplateRenderingService templateRenderingService;
    private final Map<NotificationChannel, NotificationChannelHandler> channelHandlers;

    public NotificationService(NotificationRepository notificationRepository,
                                TemplateRenderingService templateRenderingService,
                                List<NotificationChannelHandler> channelHandlers) {
        this.notificationRepository = notificationRepository;
        this.templateRenderingService = templateRenderingService;
        this.channelHandlers = channelHandlers.stream()
                .collect(Collectors.toMap(NotificationChannelHandler::channel, Function.identity()));
    }

    @Transactional
    public Notification createAndPublish(Client client, CreateNotificationRequest request) {
        Notification notification = Notification.builder()
                .client(client)
                .channel(request.channel())
                .recipient(request.recipient())
                .subject(request.subject())
                .body(resolveBody(request))
                .status(NotificationStatus.PENDING)
                .build();

        notification = notificationRepository.save(notification);
        dispatch(notification);

        return notification;
    }

    private String resolveBody(CreateNotificationRequest request) {
        boolean hasBody = request.body() != null && !request.body().isBlank();
        boolean hasTemplate = request.templateCode() != null && !request.templateCode().isBlank();

        if (hasBody == hasTemplate) {
            throw new InvalidNotificationContentException(
                    "Musí být vyplněno právě jedno z polí: body, nebo templateCode.");
        }

        if (hasBody) {
            return request.body();
        }

        return templateRenderingService.render(request.templateCode(), request.channel(), request.templateData());
    }

    @Transactional
    public Notification reprocess(Client client, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getClient().getId().equals(client.getId()))
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (notification.getStatus() != NotificationStatus.DEAD) {
            throw new NotificationNotReprocessableException(notificationId, notification.getStatus());
        }

        notification.setStatus(NotificationStatus.PENDING);
        dispatch(notification);

        return notification;
    }

    private void dispatch(Notification notification) {
        NotificationChannelHandler handler = channelHandlers.get(notification.getChannel());
        if (handler == null) {
            throw new IllegalStateException("Žádný handler pro kanál %s".formatted(notification.getChannel()));
        }
        handler.dispatch(notification);
    }
}
