package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.InventoryMovementRequestDTO;
import com.example.SmartLogi.dto.InventoryMovementResponseDTO;
import com.example.SmartLogi.dto.InventoryResponseDTO;
import com.example.SmartLogi.entities.Inventory;
import com.example.SmartLogi.entities.InventoryMovement;
import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.entities.SalesOrderLine;
import com.example.SmartLogi.enums.MovementType;
import com.example.SmartLogi.enums.OrderLineStatus;
import com.example.SmartLogi.exception.BusinessException;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.InventoryMapper;
import com.example.SmartLogi.mapper.InventoryMovementMapper;
import com.example.SmartLogi.repositories.InventoryMovementRepository;
import com.example.SmartLogi.repositories.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private InventoryMovementMapper inventoryMovementMapper;
    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryMapper mapper;


    @Test
     void  recordOutbound_ShouldThrowException_WhenStockBecomesNegative() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantityOnHand(5);
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO(1L,10);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        BusinessException exception = assertThrows(BusinessException.class , () ->{
            inventoryService.recordOutbound(dto);
        });

        assertEquals("Stock cannot be negative", exception.getMessage());
        verify(inventoryRepository, never()).save(any());
        verify(movementRepository, never()).save(any());

    }

    @Test
     void reservedQuantity_shouldReserveQuantityCorrectly() {
            Product product =  Product.builder().id(1L).build();
            Inventory inventory = Inventory.builder().id(1L)
                    .quantityOnHand(100)
                    .quantityReserved(40)
                    .product(product)
                    .build();

        SalesOrderLine salesOrderLine = SalesOrderLine.builder()
                .id(1L)
                .product(product)
                .quantityRequested(60)
                .build();

        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));

        inventoryService.reservedQuantity(salesOrderLine,1L);

        assertEquals(60, salesOrderLine.getQuantityReserved(), "Reserved quantity should match request");
        assertEquals(0, salesOrderLine.getQuantityBackorder(), "No backorder expected");
        assertEquals(OrderLineStatus.RESERVED, salesOrderLine.getStatus(), "Status should be RESERVED");

        assertEquals(100, inventory.getQuantityReserved(), "Inventory reserved count should be updated");

        verify(inventoryRepository, times(1)).save(inventory);


    }
}
