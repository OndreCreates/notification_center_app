package com.ondrecreates.notificationcenter.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(

        @NotBlank(message = "name je povinné pole")
        String name,

        @Email(message = "contactEmail musí být platná e-mailová adresa")
        String contactEmail
) {
}
