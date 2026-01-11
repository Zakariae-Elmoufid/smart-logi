package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.WarehouseRequestDTO;
import com.example.SmartLogi.dto.WarehouseResponseDTO;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.mapper.WarehouseMapper;
import com.example.SmartLogi.repositories.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseService warehouseService;

    private Warehouse warehouse;
    private WarehouseRequestDTO requestDTO;
    private WarehouseResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");
        warehouse.setCode("WH-001");
        warehouse.setAddress("123 Industrial Zone");
        warehouse.setActive(true);

        requestDTO = new WarehouseRequestDTO("Main Warehouse", "WH-001", "123 Industrial Zone", true);
        responseDTO = new WarehouseResponseDTO(1L, "Main Warehouse", "WH-001", "123 Industrial Zone", true);
    }

    @Test
    void create_ShouldReturnWarehouseResponseDTO() {
        when(warehouseMapper.toEntity(requestDTO)).thenReturn(warehouse);
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        when(warehouseMapper.toDTO(warehouse)).thenReturn(responseDTO);

        WarehouseResponseDTO result = warehouseService.create(requestDTO);

        assertNotNull(result);
        assertEquals("Main Warehouse", result.name());
        assertEquals("WH-001", result.code());
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void getAll_ShouldReturnListOfWarehouses() {
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));
        when(warehouseMapper.toDTO(warehouse)).thenReturn(responseDTO);

        List<WarehouseResponseDTO> result = warehouseService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Main Warehouse", result.get(0).name());
    }

    @Test
    void getWarehouseById_ShouldReturnWarehouse_WhenFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseMapper.toDTO(warehouse)).thenReturn(responseDTO);

        WarehouseResponseDTO result = warehouseService.getWarehouseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getWarehouseById_ShouldReturnNull_WhenNotFound() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());
        when(warehouseMapper.toDTO(null)).thenReturn(null);

        WarehouseResponseDTO result = warehouseService.getWarehouseById(99L);

        assertNull(result);
    }

    @Test
    void updateWarehouse_ShouldUpdateWarehouse_WhenFound() {
        WarehouseRequestDTO updateDTO = new WarehouseRequestDTO(
                "Updated Warehouse", "WH-002", "456 New Zone", true
        );
        WarehouseResponseDTO updatedResponse = new WarehouseResponseDTO(
                1L, "Updated Warehouse", "WH-002", "456 New Zone", true
        );

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toDTO(any(Warehouse.class))).thenReturn(updatedResponse);

        WarehouseResponseDTO result = warehouseService.updateWarehouse(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Updated Warehouse", result.name());
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void deleteWarehouse_ShouldReturnTrue_WhenWarehouseExists() {
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(warehouseRepository).deleteById(1L);

        boolean result = warehouseService.deleteWarehouse(1L);

        assertTrue(result);
        verify(warehouseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteWarehouse_ShouldReturnFalse_WhenWarehouseNotExists() {
        when(warehouseRepository.existsById(99L)).thenReturn(false);

        boolean result = warehouseService.deleteWarehouse(99L);

        assertFalse(result);
        verify(warehouseRepository, never()).deleteById(99L);
    }
}
