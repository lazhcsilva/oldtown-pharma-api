package br.com.oldtown.pharma.auth.service.impl;

import br.com.oldtown.pharma.auth.dto.LoginRequest;
import br.com.oldtown.pharma.auth.dto.LoginResponse;
import br.com.oldtown.pharma.auth.dto.RefreshTokenResponse;
import br.com.oldtown.pharma.auth.entity.RefreshToken;
import br.com.oldtown.pharma.auth.service.AuthService;
import br.com.oldtown.pharma.auth.service.JwtService;
import br.com.oldtown.pharma.auth.service.RefreshTokenService;
import br.com.oldtown.pharma.shared.exception.BadRequestException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import br.com.oldtown.pharma.user.entity.User;
import br.com.oldtown.pharma.user.mapper.UserMapper;
import br.com.oldtown.pharma.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, RefreshTokenService refreshTokenService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userMapper = userMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("Invalid credentials"));

        if (!user.isActive()) {
            throw new BadRequestException("Inactive user");
        }

        if (!validatePassword(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Password incorrect.");
        }

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new LoginResponse(accessToken,
                refreshToken.getToken(),
                "Bearer",
                900L,
                userMapper.toResponse(user));
    }

    @Override
    public void active() {

    }

    @Override
    public RefreshTokenResponse refresh(String token) {
        RefreshToken refreshToken = refreshTokenService.validate(token);
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateAccessToken(user);
        return new RefreshTokenResponse(accessToken, "Bearer", 900);
    }

    @Override
    public void logout() {

    }

    private boolean validatePassword(String passwordEntered, String passwordSaveHash) {
        return passwordEncoder.matches(passwordEntered, passwordSaveHash);
    }

}
