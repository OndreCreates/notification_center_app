package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.client.ClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/clients")
public class AdminClientController {

    private final ClientRepository clientRepository;

    public AdminClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    public List<AdminClientSummaryResponse> list() {
        return clientRepository.findAll().stream()
                .map(AdminClientSummaryResponse::from)
                .toList();
    }
}
