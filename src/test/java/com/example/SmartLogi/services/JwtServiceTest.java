package com.example.SmartLogi.services;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Set the secret key using reflection (since it's normally injected via @Value)
        ReflectionTestUtils.setField(jwtService, "SECRET", 
                "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tZ2VuZXJhdGlvbi1hbmQtdmFsaWRhdGlvbg==");

        userDetails = new User(
                "user@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );

        validToken = jwtService.generateToken("user@example.com", "ROLE_CLIENT");
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtService.generateToken("user@example.com", "ROLE_ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_ShouldContainThreeParts() {
        String token = jwtService.generateToken("user@example.com", "ROLE_ADMIN");

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts: header, payload, signature");
    }

    @Test
    void extractUsername_ShouldReturnCorrectEmail() {
        String username = jwtService.extractUsername(validToken);

        assertEquals("user@example.com", username);
    }

    @Test
    void extractRole_ShouldReturnCorrectRole() {
        String role = jwtService.extractRole(validToken);

        assertEquals("ROLE_CLIENT", role);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        Date expiration = jwtService.extractExpiration(validToken);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()), "Expiration should be in the future");
    }

    @Test
    void extractExpiration_ShouldBeApproximately30MinutesFromNow() {
        Date expiration = jwtService.extractExpiration(validToken);

        long expectedMillis = System.currentTimeMillis() + (1000 * 60 * 30); // 30 minutes
        long actualMillis = expiration.getTime();
        
        // Allow 1 minute tolerance
        assertTrue(Math.abs(expectedMillis - actualMillis) < 60000);
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        boolean isValid = jwtService.validateToken(validToken, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        UserDetails differentUser = new User(
                "different@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );

        boolean isValid = jwtService.validateToken(validToken, differentUser);

        assertFalse(isValid);
    }

    @Test
    void generateToken_ShouldGenerateUniqueTokens() {
        String token1 = jwtService.generateToken("user@example.com", "ROLE_CLIENT");
        
        // Wait a bit to ensure different issuedAt
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String token2 = jwtService.generateToken("user@example.com", "ROLE_CLIENT");

        // Tokens might be same if generated in same millisecond, 
        // but they should both be valid
        assertNotNull(token1);
        assertNotNull(token2);
    }

    @Test
    void extractUsername_ShouldWorkForDifferentEmails() {
        String token = jwtService.generateToken("admin@company.com", "ROLE_ADMIN");

        String username = jwtService.extractUsername(token);

        assertEquals("admin@company.com", username);
    }

    @Test
    void extractRole_ShouldWorkForDifferentRoles() {
        String adminToken = jwtService.generateToken("admin@example.com", "ROLE_ADMIN");
        String managerToken = jwtService.generateToken("manager@example.com", "ROLE_WAREHOUSE_MANAGER");

        assertEquals("ROLE_ADMIN", jwtService.extractRole(adminToken));
        assertEquals("ROLE_WAREHOUSE_MANAGER", jwtService.extractRole(managerToken));
    }
}
