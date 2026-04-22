package br.com.oldtown.pharma.auth.controller;

import br.com.oldtown.pharma.auth.dto.ForgotPasswordRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Password recovery")
@RestController
@RequestMapping("/api/v1/recovery")
@Valid
public class PasswordRecoveryController {

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Email String email) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok().build();
    }

}
