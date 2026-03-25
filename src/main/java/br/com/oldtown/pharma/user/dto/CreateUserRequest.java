package br.com.oldtown.pharma.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(@NotBlank(message = "First name is required") String firstName,
                                @NotBlank(message = "Last name is required") String lastName,
                                @Email(message = "Invalid email")
                                @NotBlank(message = "Email is required") String email,
                                @NotBlank(message = "Password is required")
                                @Size(min = 6, message = "Password must have at least 6 characters") String password) {
}
