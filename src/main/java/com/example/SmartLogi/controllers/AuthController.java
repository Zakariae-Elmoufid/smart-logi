package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.*;
import com.example.SmartLogi.entities.RefreshToken;
import com.example.SmartLogi.services.*;
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

import java.util.Date;
import java.util.Map;

@RestController
public class AuthController {



       @Autowired
       private  KeycloakService keycloakService;


    @PostMapping("/api/auth/register")
    public ResponseEntity<ClientResponseDTO> register(@Valid @RequestBody ClientRequestDTO dto) {
        ClientResponseDTO createdClient = keycloakService.registerClient(dto);
        return ResponseEntity.ok(createdClient);
    }

//
//
//
//    @PostMapping("/api/auth/login")
//    public ResponseEntity<?> login(@Valid @RequestBody UserRequestDTO dto) {
//
//        Authentication auth = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
//        );
//
//        if(auth.isAuthenticated()){
//            String dbRole = auth.getAuthorities()
//                    .stream()
//                    .findFirst()
//                    .map(GrantedAuthority::getAuthority)
//                    .orElseThrow(() -> new RuntimeException("user have't role !"));
//
//            String roleForToken = dbRole.startsWith("ROLE_") ? dbRole : "ROLE_" + dbRole;
//            String accessToken = jwtService.generateToken(dto.email(), roleForToken);
//
//            String refreshToken = refreshTokenService.generateRefreshToken(dto.email());
//
//            // Retourner les deux tokens
//            return ResponseEntity.ok(Map.of(
//                    "accessToken", accessToken,
//                    "refreshToken", refreshToken,
//                    "role", roleForToken,
//                    "email", dto.email()
//            ));
//        }
//         throw new UsernameNotFoundException("Invalid user request!");
//
//    }
//    @PostMapping("/api/auth/refresh-token")
//    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
//        String requestToken = request.get("refreshToken");
//
//        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(requestToken);
//
//        if(refreshToken.getExpiryDate().before(new Date())){
//            throw new RuntimeException("Refresh token expired");
//        }
//
//        String role = userService.loadUserRole(refreshToken.getUsername());
//
//        String newAccessToken = jwtService.generateToken(refreshToken.getUsername(), role);
//
//        return ResponseEntity.ok(Map.of(
//                "accessToken", newAccessToken
//        ));
//    }


}
