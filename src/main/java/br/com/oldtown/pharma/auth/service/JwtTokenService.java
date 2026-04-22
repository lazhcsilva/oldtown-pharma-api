package br.com.oldtown.pharma.auth.service;

import br.com.oldtown.pharma.auth.entity.RefreshToken;
import br.com.oldtown.pharma.auth.repository.RefreshTokenRepository;
import br.com.oldtown.pharma.auth.security.JwtProperties;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import br.com.oldtown.pharma.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public JwtTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    public RefreshToken create(User user) {
        String value = UUID.randomUUID().toString();

        LocalDateTime expireAt = LocalDateTime.now()
                .plusNanos(jwtProperties.refreshTokenExpirationDays() * 1_000_000);

        RefreshToken refreshToken = new RefreshToken(value, user, expireAt);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid Refresh token"));

        if (!refreshToken.isValid()) {
            throw new ConflictException("Expired or revoked token");
        }

        return refreshToken;
    }

    public void revoke(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid refresh token"));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }

}
