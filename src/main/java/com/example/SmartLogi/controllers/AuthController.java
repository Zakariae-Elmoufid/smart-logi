package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.*;
import com.example.SmartLogi.services.ClientService;
import com.example.SmartLogi.services.JwtService;
import com.example.SmartLogi.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.ok(jwtService.generateToken(dto.email()));
        }
         throw new UsernameNotFoundException("Invalid user request!");

//        SecurityContextHolder.getContext().setAuthentication(auth);

    }

}
