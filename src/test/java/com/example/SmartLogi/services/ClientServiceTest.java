package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ClientRequestDTO;
import com.example.SmartLogi.dto.ClientResponseDTO;
import com.example.SmartLogi.entities.Client;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.mapper.ClientMapper;
import com.example.SmartLogi.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientRequestDTO requestDTO;
    private ClientResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .phoneNumber("0612345678")
                .role(UserRole.CLIENT)
                .enabled(true)
                .build();

        requestDTO = new ClientRequestDTO(
                "John", "Doe", "john.doe@example.com", 
                "password123", "0612345678"
        );

        // ClientResponseDTO: id, firstName, lastName, email, createdAt, phoneNumber, role, active
        responseDTO = new ClientResponseDTO(
                1L, "John", "Doe", "john.doe@example.com", 
                LocalDateTime.now(), "0612345678", "CLIENT", true
        );
    }

    @Test
    void createClient_ShouldReturnClientResponseDTO() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toDto(any(Client.class))).thenReturn(responseDTO);

        ClientResponseDTO result = clientService.createClient(requestDTO);

        assertNotNull(result);
        assertEquals("John", result.firstName());
        assertEquals("john.doe@example.com", result.email());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void createClient_ShouldEncodePassword() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toDto(any(Client.class))).thenReturn(responseDTO);

        clientService.createClient(requestDTO);

        verify(passwordEncoder).encode("password123");
    }

    @Test
    void createClient_ShouldSetRoleAsClient() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0);
            assertEquals(UserRole.CLIENT, savedClient.getRole());
            return client;
        });
        when(clientMapper.toDto(any(Client.class))).thenReturn(responseDTO);

        clientService.createClient(requestDTO);

        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void createClient_ShouldSetEnabledTrue() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0);
            assertTrue(savedClient.isEnabled());
            return client;
        });
        when(clientMapper.toDto(any(Client.class))).thenReturn(responseDTO);

        clientService.createClient(requestDTO);

        verify(clientRepository).save(any(Client.class));
    }
}
