package br.com.oldtown.pharma.auth.dto;

import br.com.oldtown.pharma.user.dto.UserResponse;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserResponse user
) {
}
