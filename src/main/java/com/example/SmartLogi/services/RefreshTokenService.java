package com.example.SmartLogi.services;

import com.example.SmartLogi.entities.RefreshToken;
import com.example.SmartLogi.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateRefreshToken(String username){
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken =  RefreshToken.builder()
        .token(token).username(username).revoked(false)
        .expiryDate(LocalDateTime.now().plus(7,ChronoUnit.DAYS))
        .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public RefreshToken validateRefreshToken(String token){
        RefreshToken refreshToken =  refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found or revoked"));
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }
        return refreshToken;

    }

    public void  revoke(String token){
      RefreshToken refreshToken =  refreshTokenRepository.findByTokenAndRevokedFalse(token)
              .orElseThrow(() -> new RuntimeException("Refresh token not found or revoked"));
      refreshToken.setRevoked(true);
      refreshTokenRepository.save(refreshToken);
    }
}
