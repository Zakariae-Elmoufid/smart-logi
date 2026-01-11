package com.example.SmartLogi.services;

import com.example.SmartLogi.entities.RefreshToken;
import com.example.SmartLogi.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private RefreshToken refreshToken;
    private String tokenString;

    @BeforeEach
    void setUp() {
        tokenString = UUID.randomUUID().toString();
        refreshToken = RefreshToken.builder()
                .id(1L)
                .username("john.doe@example.com")
                .token(tokenString)
                .revoked(false)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
    }

    @Test
    void generateRefreshToken_ShouldReturnTokenString() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = refreshTokenService.generateRefreshToken("john.doe@example.com");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void generateRefreshToken_ShouldSaveTokenWithCorrectUsername() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> {
                    RefreshToken saved = invocation.getArgument(0);
                    assertEquals("john.doe@example.com", saved.getUsername());
                    assertFalse(saved.isRevoked());
                    return saved;
                });

        refreshTokenService.generateRefreshToken("john.doe@example.com");

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void validateRefreshToken_ShouldReturnToken_WhenValid() {
        when(refreshTokenRepository.findByTokenAndRevokedFalse(tokenString))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.validateRefreshToken(tokenString);

        assertNotNull(result);
        assertEquals(tokenString, result.getToken());
        assertEquals("john.doe@example.com", result.getUsername());
    }

    @Test
    void validateRefreshToken_ShouldThrowException_WhenTokenNotFound() {
        when(refreshTokenRepository.findByTokenAndRevokedFalse("invalid-token"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> refreshTokenService.validateRefreshToken("invalid-token"));
    }

    @Test
    void validateRefreshToken_ShouldThrowException_WhenTokenExpired() {
        RefreshToken expiredToken = RefreshToken.builder()
                .id(1L)
                .username("john.doe@example.com")
                .token(tokenString)
                .revoked(false)
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();

        when(refreshTokenRepository.findByTokenAndRevokedFalse(tokenString))
                .thenReturn(Optional.of(expiredToken));

        assertThrows(RuntimeException.class, 
            () -> refreshTokenService.validateRefreshToken(tokenString));
    }

    @Test
    void revoke_ShouldSetRevokedTrue() {
        when(refreshTokenRepository.findByTokenAndRevokedFalse(tokenString))
                .thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        refreshTokenService.revoke(tokenString);

        assertTrue(refreshToken.isRevoked());
        verify(refreshTokenRepository, times(1)).save(refreshToken);
    }

    @Test
    void revoke_ShouldThrowException_WhenTokenNotFound() {
        when(refreshTokenRepository.findByTokenAndRevokedFalse("invalid-token"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> refreshTokenService.revoke("invalid-token"));
        
        verify(refreshTokenRepository, never()).save(any());
    }
}
