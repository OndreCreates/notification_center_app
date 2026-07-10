package com.ondrecreates.notificationcenter.admin;

import com.ondrecreates.notificationcenter.common.NotFoundException;

public class ClientNotFoundException extends NotFoundException {

    public ClientNotFoundException(Long id) {
        super("Klient %d nenalezen".formatted(id));
    }
}
