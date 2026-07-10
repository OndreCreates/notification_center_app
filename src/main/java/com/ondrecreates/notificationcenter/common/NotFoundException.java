package com.ondrecreates.notificationcenter.common;

/**
 * Společný předek pro "entita nenalezena" výjimky napříč doménami
 * (notifikace, šablona, klient) – GlobalExceptionHandler je mapuje na 404
 * jedním handlerem místo tří skoro identických.
 */
public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(String message) {
        super(message);
    }
}
