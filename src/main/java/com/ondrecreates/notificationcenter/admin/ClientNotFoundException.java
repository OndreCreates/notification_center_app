package com.ondrecreates.notificationcenter.admin;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long id) {
        super("Klient %d nenalezen".formatted(id));
    }
}
