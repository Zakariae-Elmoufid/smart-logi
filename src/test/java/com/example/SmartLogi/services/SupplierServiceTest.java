package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.SupplierRequestDTO;
import com.example.SmartLogi.dto.SupplierResponseDTO;
import com.example.SmartLogi.entities.Supplier;
import com.example.SmartLogi.exception.BusinessException;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.SupplierMapper;
import com.example.SmartLogi.repositories.SupplierRepository;
import org.apache.coyote.BadRequestException;
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
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private SupplierRequestDTO requestDTO;
    private SupplierResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .supplierName("Supplier One")
                .phoneNumber("0612345678")
                .build();

        // SupplierRequestDTO: supplierName, phoneNumber
        requestDTO = new SupplierRequestDTO("Supplier One", "0612345678");
        // SupplierResponseDTO: id, supplierName, phoneNumber
        responseDTO = new SupplierResponseDTO(1L, "Supplier One", "0612345678");
    }

    @Test
    void create_ShouldReturnSupplierResponseDTO_WhenValidInput() {
        when(supplierRepository.existsByPhoneNumber(requestDTO.phoneNumber())).thenReturn(false);
        when(supplierMapper.toEntity(requestDTO)).thenReturn(supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toDto(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.create(requestDTO);

        assertNotNull(result);
        assertEquals("Supplier One", result.supplierName());
        verify(supplierRepository, times(1)).save(supplier);
    }

    @Test
    void create_ShouldThrowException_WhenPhoneNumberExists() {
        when(supplierRepository.existsByPhoneNumber(requestDTO.phoneNumber())).thenReturn(true);

        assertThrows(BusinessException.class, () -> supplierService.create(requestDTO));
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void getAll_ShouldReturnListOfSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));
        when(supplierMapper.toDto(supplier)).thenReturn(responseDTO);

        List<SupplierResponseDTO> result = supplierService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Supplier One", result.get(0).supplierName());
    }

    @Test
    void getById_ShouldReturnSupplier_WhenFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toDto(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> supplierService.getById(99L));
    }

    @Test
    void update_ShouldUpdateSupplier_WhenFound() {
        SupplierRequestDTO updateDTO = new SupplierRequestDTO("Updated Supplier", "0698765432");
        SupplierResponseDTO updatedResponse = new SupplierResponseDTO(1L, "Updated Supplier", "0698765432");

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        doNothing().when(supplierMapper).updateEntityFromDto(updateDTO, supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toDto(supplier)).thenReturn(updatedResponse);

        SupplierResponseDTO result = supplierService.update(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Updated Supplier", result.supplierName());
        verify(supplierRepository, times(1)).save(supplier);
    }

    @Test
    void update_ShouldThrowException_WhenSupplierNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> supplierService.update(99L, requestDTO));
    }

    @Test
    void delete_ShouldDeleteSupplier_WhenFound() throws BadRequestException {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        doNothing().when(supplierRepository).delete(supplier);
        when(supplierMapper.toDto(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.delete(1L);

        assertNotNull(result);
        verify(supplierRepository, times(1)).delete(supplier);
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supplierService.delete(99L));
    }
}
