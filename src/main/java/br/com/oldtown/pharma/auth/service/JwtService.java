package br.com.oldtown.pharma.auth.service;

import br.com.oldtown.pharma.auth.security.JwtProperties;
import br.com.oldtown.pharma.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        if (jwtProperties.secret() == null || jwtProperties.secret().isBlank()) {
            throw new IllegalStateException("The security.jwt.secret property has not been configured.");
        }

        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.accessTokenExpirationMinutes());

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claims(Map.of(
                        "userId", user.getId(),
                        "role", user.getRole().name()
                ))
                .signWith(secretKey)
                .compact();
    }
}
