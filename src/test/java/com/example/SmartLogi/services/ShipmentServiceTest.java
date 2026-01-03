package com.example.SmartLogi.services;

import com.example.SmartLogi.entities.Carrier;
import com.example.SmartLogi.entities.Shipment;
import com.example.SmartLogi.enums.ShipmentStatus;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.repositories.CarrierRepository;
import com.example.SmartLogi.repositories.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private CarrierRepository carrierRepository;

    @InjectMocks
    private ShipmentService shipmentService;

    private Shipment shipment;
    private Carrier carrier;

    @BeforeEach
    void setUp() {
        carrier = new Carrier();
        carrier.setId(1L);
        carrier.setCarrierName("DHL Express");
        carrier.setPhoneNumber("0612345678");

        shipment = Shipment.builder()
                .id(1)
                .trackingNumber("TRACK-001")
                .shipmentStatus(ShipmentStatus.PLANNED)
                .plannedDate(LocalDateTime.now().plusDays(3))
                .build();
    }

    @Test
    void assignCarrier_ShouldAssignCarrierToShipment_WhenBothExist() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        when(shipmentRepository.save(shipment)).thenReturn(shipment);

        shipmentService.assignCarrier(1L, 1L);

        assertEquals(carrier, shipment.getCarrier());
        verify(shipmentRepository, times(1)).save(shipment);
    }

    @Test
    void assignCarrier_ShouldThrowException_WhenShipmentNotFound() {
        when(shipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> shipmentService.assignCarrier(99L, 1L));

        verify(carrierRepository, never()).findById(any());
        verify(shipmentRepository, never()).save(any());
    }

    @Test
    void assignCarrier_ShouldThrowException_WhenCarrierNotFound() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(carrierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> shipmentService.assignCarrier(1L, 99L));

        verify(shipmentRepository, never()).save(any());
    }

    @Test
    void assignCarrier_ShouldUpdateExistingCarrier() {
        Carrier existingCarrier = new Carrier();
        existingCarrier.setId(2L);
        existingCarrier.setCarrierName("FedEx");
        shipment.setCarrier(existingCarrier);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        when(shipmentRepository.save(shipment)).thenReturn(shipment);

        shipmentService.assignCarrier(1L, 1L);

        assertEquals(carrier, shipment.getCarrier());
        assertEquals("DHL Express", shipment.getCarrier().getCarrierName());
        verify(shipmentRepository, times(1)).save(shipment);
    }
}
