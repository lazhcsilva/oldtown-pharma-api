package br.com.oldtown.pharma.auth.service.impl;

import br.com.oldtown.pharma.auth.dto.RefreshTokenResponse;
import br.com.oldtown.pharma.auth.entity.RefreshToken;
import br.com.oldtown.pharma.auth.repository.RefreshTokenRepository;
import br.com.oldtown.pharma.auth.security.JwtProperties;
import br.com.oldtown.pharma.auth.service.JwtService;
import br.com.oldtown.pharma.auth.service.RefreshTokenService;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import br.com.oldtown.pharma.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties,
                                   JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
        this.jwtService = jwtService;
    }

    @Override
    public RefreshToken create(User user) {
        String value = UUID.randomUUID().toString();

        LocalDateTime expireAt = LocalDateTime.now().plusNanos(jwtProperties.refreshTokenExpirationDays() * 1_000_000);
        RefreshToken refreshToken = new RefreshToken(value, user, expireAt);
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken validate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new RuntimeException("Expired or revoked refresh token");
        }

        return refreshToken;
    }

    @Override
    public void revoke(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid refresh token"));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }
}
