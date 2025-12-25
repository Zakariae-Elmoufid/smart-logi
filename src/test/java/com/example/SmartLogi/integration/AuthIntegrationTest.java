package com.example.SmartLogi.integration;

import com.example.SmartLogi.dto.UserRequestDTO;
import com.example.SmartLogi.entities.User;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.repositories.RefreshTokenRepository;
import com.example.SmartLogi.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;   
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@Transactional
public class AuthIntegrationTest  {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired private PasswordEncoder encoder;


    private String accessToken;
    private String refreshToken;


    @BeforeEach
    public void setUp() {
        User user = new User();
         user.setFirstName("Test");
         user.setLastName("User");
        user.setEmail("admin@test.com");
        user.setPassword(encoder.encode("123456"));

        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
    }

    @Test
    public void shouldLoginAndReturnTokens() throws Exception {
        UserRequestDTO request = new UserRequestDTO("admin@test.com", "123456");


        String response =  mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(" RESULT TEST = " + response);

    }

    @Test
    public void shouldFailLoginWithBadCredentials() throws Exception {
        UserRequestDTO request = new UserRequestDTO("admin@test.com", "123q33456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndGetToken() throws Exception{
        UserRequestDTO request = new UserRequestDTO("admin@test.com", "123456");

        var response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(response);

        var json = mapper.readTree(response);
        accessToken = json.get("accessToken").asText();
        refreshToken = json.get("refreshToken").asText();
        return accessToken;
    }


    // ACCESS PROTECTED WITH JWT
    @Test
    public void shouldAccessProtectedEndpointWithValidToken() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/admin/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFailIfNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        String oldToken = loginAndGetToken();

        var response = mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(" REFRESH RESULT = " + response);
    }



}
