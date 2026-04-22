package br.com.oldtown.pharma.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Inform email")
        @Email
        String email,

        @NotBlank(message = "Inform password")
        String password
) {
}
