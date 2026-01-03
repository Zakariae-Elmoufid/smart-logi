package com.example.SmartLogi.services;

import com.example.SmartLogi.entities.BlacklistedToken;
import com.example.SmartLogi.repositories.BlacklistedTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private String validToken;
    private Date futureDate;

    @BeforeEach
    void setUp() {
        validToken = "valid.jwt.token";
        futureDate = new Date(System.currentTimeMillis() + 1800000); // 30 minutes from now
    }

    @Test
    void blacklistToken_ShouldSaveToken_WhenNotAlreadyBlacklisted() {
        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(false);
        when(jwtService.extractExpiration(validToken)).thenReturn(futureDate);
        when(blacklistedTokenRepository.save(any(BlacklistedToken.class)))
                .thenReturn(new BlacklistedToken(validToken, futureDate));

        tokenBlacklistService.blacklistToken(validToken);

        verify(blacklistedTokenRepository, times(1)).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklistToken_ShouldNotSave_WhenTokenAlreadyBlacklisted() {
        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(true);

        tokenBlacklistService.blacklistToken(validToken);

        verify(blacklistedTokenRepository, never()).save(any());
    }

    @Test
    void blacklistToken_ShouldNotSave_WhenTokenIsNull() {
        tokenBlacklistService.blacklistToken(null);

        verify(blacklistedTokenRepository, never()).existsByToken(any());
        verify(blacklistedTokenRepository, never()).save(any());
    }

    @Test
    void blacklistToken_ShouldSaveWithCorrectExpiryDate() {
        ArgumentCaptor<BlacklistedToken> tokenCaptor = ArgumentCaptor.forClass(BlacklistedToken.class);
        
        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(false);
        when(jwtService.extractExpiration(validToken)).thenReturn(futureDate);
        when(blacklistedTokenRepository.save(any(BlacklistedToken.class)))
                .thenReturn(new BlacklistedToken(validToken, futureDate));

        tokenBlacklistService.blacklistToken(validToken);

        verify(blacklistedTokenRepository).save(tokenCaptor.capture());
        assertEquals(futureDate, tokenCaptor.getValue().getExpiryDate());
    }

    @Test
    void isTokenBlacklisted_ShouldReturnTrue_WhenTokenIsBlacklisted() {
        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(true);

        boolean result = tokenBlacklistService.isTokenBlacklisted(validToken);

        assertTrue(result);
    }

    @Test
    void isTokenBlacklisted_ShouldReturnFalse_WhenTokenIsNotBlacklisted() {
        when(blacklistedTokenRepository.existsByToken(validToken)).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenBlacklisted(validToken);

        assertFalse(result);
    }

    @Test
    void cleanupExpiredTokens_ShouldCallDeleteExpiredTokens() {
        doNothing().when(blacklistedTokenRepository).deleteExpiredTokens(any(Date.class));

        tokenBlacklistService.cleanupExpiredTokens();

        verify(blacklistedTokenRepository, times(1)).deleteExpiredTokens(any(Date.class));
    }
}
