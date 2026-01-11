package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.CarrierRequestDTO;
import com.example.SmartLogi.dto.CarrierResponseDTO;
import com.example.SmartLogi.entities.Carrier;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.CarrierMapper;
import com.example.SmartLogi.repositories.CarrierRepository;
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
class CarrierServiceTest {

    @Mock
    private CarrierRepository carrierRepository;

    @Mock
    private CarrierMapper carrierMapper;

    @InjectMocks
    private CarrierService carrierService;

    private Carrier carrier;
    private CarrierRequestDTO requestDTO;
    private CarrierResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        carrier = new Carrier();
        carrier.setId(1L);
        carrier.setCarrierName("DHL Express");
        carrier.setPhoneNumber("0612345678");

        requestDTO = new CarrierRequestDTO("DHL Express", "0612345678");
        responseDTO = new CarrierResponseDTO(1L, "DHL Express", "0612345678");
    }

    @Test
    void create_ShouldReturnCarrierResponseDTO_WhenValidInput() {
        when(carrierMapper.toEntity(requestDTO)).thenReturn(carrier);
        when(carrierRepository.save(carrier)).thenReturn(carrier);
        when(carrierMapper.toDTO(carrier)).thenReturn(responseDTO);

        CarrierResponseDTO result = carrierService.create(requestDTO);

        assertNotNull(result);
        assertEquals("DHL Express", result.carrierName());
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void getAll_ShouldReturnListOfCarriers() {
        when(carrierRepository.findAll()).thenReturn(List.of(carrier));
        when(carrierMapper.toDTO(carrier)).thenReturn(responseDTO);

        List<CarrierResponseDTO> result = carrierService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DHL Express", result.get(0).carrierName());
    }

    @Test
    void getById_ShouldReturnCarrier_WhenFound() {
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        when(carrierMapper.toDTO(carrier)).thenReturn(responseDTO);

        CarrierResponseDTO result = carrierService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        when(carrierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carrierService.getById(99L));
    }

    @Test
    void update_ShouldUpdateCarrier_WhenFound() {
        CarrierRequestDTO updateDTO = new CarrierRequestDTO("FedEx", "0698765432");
        CarrierResponseDTO updatedResponse = new CarrierResponseDTO(1L, "FedEx", "0698765432");

        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);
        when(carrierMapper.toDTO(any(Carrier.class))).thenReturn(updatedResponse);

        CarrierResponseDTO result = carrierService.update(1L, updateDTO);

        assertNotNull(result);
        assertEquals("FedEx", result.carrierName());
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void update_ShouldThrowException_WhenCarrierNotFound() {
        when(carrierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> carrierService.update(99L, requestDTO));
    }

    @Test
    void delete_ShouldDeleteCarrier_WhenFound() {
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        doNothing().when(carrierRepository).delete(carrier);

        carrierService.delete(1L);

        verify(carrierRepository, times(1)).delete(carrier);
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        when(carrierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carrierService.delete(99L));
    }
}
