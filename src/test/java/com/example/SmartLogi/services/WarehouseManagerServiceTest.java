package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ManagerCreateDTO;
import com.example.SmartLogi.dto.ManagerResponseDTO;
import com.example.SmartLogi.dto.ManagerUpdateDTO;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.entities.WarehouseManager;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.mapper.ManagerMapper;
import com.example.SmartLogi.repositories.WarehouseManagerRepository;
import com.example.SmartLogi.repositories.WarehouseRepository;
import com.example.SmartLogi.util.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseManagerServiceTest {

    @Mock
    private WarehouseManagerRepository warehouseManagerRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ManagerMapper managerMapper;

    @InjectMocks
    private WarehouseManagerService warehouseManagerService;

    private WarehouseManager warehouseManager;
    private Warehouse warehouse;
    private ManagerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");

        warehouseManager = WarehouseManager.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.manager@example.com")
                .password("hashedPassword")
                .role(UserRole.WAREHOUSE_MANAGER)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        // ManagerResponseDTO: id, firstName, lastName, email, createdAt, role, active
        responseDTO = new ManagerResponseDTO(
                1L, "John", "Doe", "john.manager@example.com", 
                LocalDateTime.now(), "WAREHOUSE_MANAGER", true
        );
    }

    @Test
    void create_ShouldReturnManagerResponseDTO() {
        // ManagerCreateDTO: firstName, lastName, email, password, active, warehouseIds
        ManagerCreateDTO createDTO = new ManagerCreateDTO(
                "John", "Doe", "john.manager@example.com", "password123", true, Set.of(1L)
        );

        when(warehouseRepository.findAllById(Set.of(1L))).thenReturn(List.of(warehouse));
        when(warehouseManagerRepository.save(any(WarehouseManager.class))).thenReturn(warehouseManager);
        when(managerMapper.toDTO(warehouseManager)).thenReturn(responseDTO);

        try (MockedStatic<PasswordUtils> mockedPasswordUtils = mockStatic(PasswordUtils.class)) {
            mockedPasswordUtils.when(() -> PasswordUtils.hash("password123"))
                    .thenReturn("hashedPassword");

            ManagerResponseDTO result = warehouseManagerService.create(createDTO);

            assertNotNull(result);
            assertEquals("John", result.firstName());
            verify(warehouseManagerRepository, times(1)).save(any(WarehouseManager.class));
        }
    }

    @Test
    void getManagerById_ShouldReturnManager_WhenFound() {
        when(warehouseManagerRepository.findById(1L)).thenReturn(Optional.of(warehouseManager));
        when(managerMapper.toDTO(warehouseManager)).thenReturn(responseDTO);

        ManagerResponseDTO result = warehouseManagerService.getManagerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getManagerById_ShouldReturnNull_WhenNotFound() {
        when(warehouseManagerRepository.findById(99L)).thenReturn(Optional.empty());
        when(managerMapper.toDTO(null)).thenReturn(null);

        ManagerResponseDTO result = warehouseManagerService.getManagerById(99L);

        assertNull(result);
    }

    @Test
    void updateManager_ShouldUpdateAndReturnManager() {
        // ManagerUpdateDTO: firstName, lastName, email, active, warehouseIds
        ManagerUpdateDTO updateDTO = new ManagerUpdateDTO(
                "Jane", "Smith", "jane.smith@example.com", true, Set.of(1L)
        );
        // ManagerResponseDTO: id, firstName, lastName, email, createdAt, role, active
        ManagerResponseDTO updatedResponse = new ManagerResponseDTO(
                1L, "Jane", "Smith", "jane.smith@example.com", 
                LocalDateTime.now(), "WAREHOUSE_MANAGER", true
        );

        when(warehouseManagerRepository.findById(1L)).thenReturn(Optional.of(warehouseManager));
        when(warehouseManagerRepository.save(warehouseManager)).thenReturn(warehouseManager);
        when(managerMapper.toDTO(warehouseManager)).thenReturn(updatedResponse);

        ManagerResponseDTO result = warehouseManagerService.updateManager(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Jane", result.firstName());
        verify(warehouseManagerRepository, times(1)).save(warehouseManager);
    }
}
