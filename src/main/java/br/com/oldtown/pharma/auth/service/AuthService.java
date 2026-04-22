package br.com.oldtown.pharma.auth.service;

import br.com.oldtown.pharma.auth.dto.LoginRequest;
import br.com.oldtown.pharma.auth.dto.LoginResponse;
import br.com.oldtown.pharma.auth.dto.RefreshTokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RefreshTokenResponse refresh(String token);
    void logout();
    void active();
}
