package com.ondrecreates.notificationcenter.admin;

/**
 * apiKey nese plaintext klíč – existuje jen v tomto jednom response, nikde se
 * neukládá ani nedá znovu zobrazit (v DB je jen hash). Klient si ho musí
 * uložit hned teď.
 */
public record CreateClientResponse(Long id, String name, String apiKey) {
}
