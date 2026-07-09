package com.ondrecreates.notificationcenter.admin;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/clients")
public class AdminClientController {

    private final AdminClientService adminClientService;

    public AdminClientController(AdminClientService adminClientService) {
        this.adminClientService = adminClientService;
    }

    @GetMapping
    public List<AdminClientSummaryResponse> list() {
        return adminClientService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateClientResponse create(@Valid @RequestBody CreateClientRequest request) {
        return adminClientService.create(request);
    }

    @PatchMapping("/{id}")
    public AdminClientSummaryResponse setActive(@PathVariable Long id, @Valid @RequestBody SetClientActiveRequest request) {
        return adminClientService.setActive(id, request.active());
    }
}
