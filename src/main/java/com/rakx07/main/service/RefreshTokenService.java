package com.rakx07.main.service;

import com.rakx07.main.model.RefreshToken;
import com.rakx07.main.model.User;
import com.rakx07.main.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh.expiry}")
    private long JWT_REFRESH_EXP;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    public boolean isRefreshTokenValid(RefreshToken refreshToken) {
        return refreshToken.getExpDate().compareTo(Instant.now()) > 0;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpDate(Instant.now().plusMillis(JWT_REFRESH_EXP));

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

}
