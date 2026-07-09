package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.client.Client;

public record AdminClientSummaryResponse(Long id, String name, boolean active) {

    static AdminClientSummaryResponse from(Client client) {
        return new AdminClientSummaryResponse(client.getId(), client.getName(), client.isActive());
    }
}
