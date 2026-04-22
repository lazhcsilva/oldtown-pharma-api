package br.com.oldtown.pharma.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String tokenType,
        long expireIn
) {
}
