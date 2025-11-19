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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private ClientRequestDTO clientRequestDTO;
    private Client client;
    private Client savedClient;
    private ClientResponseDTO clientResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        clientRequestDTO = ClientRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .phoneNumber("+1234567890")
                .build();

        client = new Client();
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");
        client.setPhoneNumber("+1234567890");
        client.setRole(UserRole.CLIENT);

        savedClient = new Client();
        savedClient.setId(1L);
        savedClient.setFirstName("John");
        savedClient.setLastName("Doe");
        savedClient.setEmail("john.doe@example.com");
        savedClient.setPhoneNumber("+1234567890");
        savedClient.setRole(UserRole.CLIENT);

        clientResponseDTO = new ClientResponseDTO(
                1L,
                "John",
                "Doe",
                "john.doe@example.com"
        );
    }

    @Test
    void createClient_ShouldReturnClientResponseDTO_WhenValidRequestProvided() {
        // Arrange
        when(clientMapper.toEntity(any(ClientRequestDTO.class))).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        when(clientMapper.toDto(any(Client.class))).thenReturn(clientResponseDTO);

        // Act
        ClientResponseDTO result = clientService.createClient(clientRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("john.doe@example.com", result.email());

        // Verify interactions
        verify(clientMapper, times(1)).toEntity(clientRequestDTO);
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(clientMapper, times(1)).toDto(savedClient);
    }

    @Test
    void createClient_ShouldHashPassword_BeforeSaving() {
        // Arrange
        when(clientMapper.toEntity(any(ClientRequestDTO.class))).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        when(clientMapper.toDto(any(Client.class))).thenReturn(clientResponseDTO);

        // Act
        clientService.createClient(clientRequestDTO);

        // Assert - verify that password was set (hashed) before saving
        verify(clientRepository).save(argThat(savedClientArg -> {
            assertNotNull(savedClientArg.getPassword(), "Password should be set");
            assertNotEquals("password123", savedClientArg.getPassword(), 
                "Password should be hashed, not plain text");
            return true;
        }));
    }

    @Test
    void createClient_ShouldMapFieldsCorrectly() {
        // Arrange
        when(clientMapper.toEntity(any(ClientRequestDTO.class))).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        when(clientMapper.toDto(any(Client.class))).thenReturn(clientResponseDTO);

        // Act
        ClientResponseDTO result = clientService.createClient(clientRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(clientRequestDTO.firstName(), result.firstName());
        assertEquals(clientRequestDTO.lastName(), result.lastName());
        assertEquals(clientRequestDTO.email(), result.email());
    }
}
