package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.client.Client;
import com.ondrecreates.notificationcenter.client.ClientRepository;
import com.ondrecreates.notificationcenter.security.ApiKeyHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminClientService {

    private final ClientRepository clientRepository;

    public AdminClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminClientSummaryResponse> list() {
        return clientRepository.findAll().stream()
                .map(AdminClientSummaryResponse::from)
                .toList();
    }

    @Transactional
    public CreateClientResponse create(CreateClientRequest request) {
        String apiKey = UUID.randomUUID().toString();

        Client client = Client.builder()
                .name(request.name())
                .apiKeyHash(ApiKeyHasher.hash(apiKey))
                .contactEmail(request.contactEmail())
                .active(true)
                .build();

        client = clientRepository.save(client);

        return new CreateClientResponse(client.getId(), client.getName(), apiKey);
    }

    @Transactional
    public AdminClientSummaryResponse setActive(Long clientId, boolean active) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        client.setActive(active);

        return AdminClientSummaryResponse.from(client);
    }
}
