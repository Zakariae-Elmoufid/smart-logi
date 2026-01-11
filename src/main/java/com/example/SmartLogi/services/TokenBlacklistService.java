package com.example.SmartLogi.services;

import com.example.SmartLogi.entities.BlacklistedToken;
import com.example.SmartLogi.repositories.BlacklistedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtService jwtService;

    public TokenBlacklistService(BlacklistedTokenRepository blacklistedTokenRepository, JwtService jwtService) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.jwtService = jwtService;
    }

    /**
     * Blacklist an access token (usually on logout)
     */
    public void blacklistToken(String token) {
        if (token != null && !isTokenBlacklisted(token)) {
            Date expiryDate = jwtService.extractExpiration(token);
            BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiryDate);
            blacklistedTokenRepository.save(blacklistedToken);
        }
    }

    /**
     * Check if a token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    /**
     * Clean up expired blacklisted tokens every hour
     * Tokens that have already expired don't need to stay in the blacklist
     */
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        blacklistedTokenRepository.deleteExpiredTokens(new Date());
    }
}
