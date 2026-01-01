package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.*;
import com.example.SmartLogi.entities.RefreshToken;
import com.example.SmartLogi.services.ClientService;
import com.example.SmartLogi.services.JwtService;
import com.example.SmartLogi.services.RefreshTokenService;
import com.example.SmartLogi.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;



    @PostMapping("/api/auth/register")
    public ResponseEntity<ClientResponseDTO> register(@Valid @RequestBody ClientRequestDTO dto) {
        ClientResponseDTO createdClient = clientService.createClient(dto);
        return ResponseEntity.ok(createdClient);
    }




    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequestDTO dto) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        if(auth.isAuthenticated()){
            String dbRole = auth.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElseThrow(() -> new RuntimeException("user have't role !"));

            String roleForToken = dbRole.startsWith("ROLE_") ? dbRole : "ROLE_" + dbRole;
            String accessToken = jwtService.generateToken(dto.email(), roleForToken);

            String refreshToken = refreshTokenService.generateRefreshToken(dto.email());

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "role", roleForToken,
                    "email", dto.email()
            ));
        }
         throw new UsernameNotFoundException("Invalid user request!");

    }
    @PostMapping("/api/auth/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestToken = request.get("refreshToken");

        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(requestToken);

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Refresh token expired");
        }

        String role = userService.loadUserRole(refreshToken.getUsername());

        String newAccessToken = jwtService.generateToken(refreshToken.getUsername(), role);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        refreshTokenService.revoke(refreshToken);

        return ResponseEntity.ok("Logged out successfully");
    }








}
