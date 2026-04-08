package br.com.oldtown.pharma.user.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Is necessary inform old Password")
        String oldPassword,

        @NotBlank(message = "Is necessary a new Password")
        String newPassword,

        @NotBlank(message = "Is necessary confirm new Password")
        String confirmNewPassword
) {
}
