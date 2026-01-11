package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.InventoryMovementRequestDTO;
import com.example.SmartLogi.entities.*;
import com.example.SmartLogi.enums.OrderLineStatus;
import com.example.SmartLogi.exception.BusinessException;
import com.example.SmartLogi.mapper.InventoryMapper;
import com.example.SmartLogi.mapper.InventoryMovementMapper;
import com.example.SmartLogi.repositories.InventoryMovementRepository;
import com.example.SmartLogi.repositories.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hibernate.internal.util.collections.ArrayHelper.forEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO(inventory.getId(),10);

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

    @Test
    void  releaseInventory_shouldReservedQuantityCorrectly() {
        Product product =  Product.builder().id(1L).build();
        Inventory inventory = Inventory.builder().id(1L)
                .quantityOnHand(100)
                .quantityReserved(60)
                .product(product)
                .build();
        SalesOrderLine salesOrderLine = SalesOrderLine.builder()
                .id(1L)
                .product(product)
                .quantityReserved(40)
                .build();
        SalesOrder salesOrder = SalesOrder.builder().id(1L)
                .orderLines(List.of(salesOrderLine))
                .warehouse(Warehouse.builder().id(1L).build())
                .build();
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));
        inventoryService.releaseInventory(salesOrder);
        assertEquals(20, inventory.getQuantityReserved(),
                "quantityReserved must be decreased by line.quantityReserved");
        verify(inventoryRepository, times(1)).save(inventory);

    }




}
