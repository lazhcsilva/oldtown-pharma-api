package br.com.oldtown.pharma.auth.service;

import br.com.oldtown.pharma.auth.entity.RefreshToken;
import br.com.oldtown.pharma.user.entity.User;

public interface RefreshTokenService {
    RefreshToken create(User user);
    RefreshToken validate(String token);
    void revoke(String token);
}
