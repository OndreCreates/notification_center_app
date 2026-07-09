package com.ondrecreates.notificationcenter.client;

import com.ondrecreates.notificationcenter.security.ApiKeyHasher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class DevClientSeeder implements CommandLineRunner {

    private final ClientRepository clientRepository;

    public DevClientSeeder(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void run(String... args) {
        if (clientRepository.count() > 0) {
            return;
        }

        String apiKey = UUID.randomUUID().toString();
        Client client = Client.builder()
                .name("Dev Test Client")
                .apiKeyHash(ApiKeyHasher.hash(apiKey))
                .contactEmail("dev@example.com")
                .active(true)
                .build();
        clientRepository.save(client);

        log.info("Vytvořen výchozí dev klient. API klíč (zobrazí se jen teď, ulož si ho): {}", apiKey);
    }
}
