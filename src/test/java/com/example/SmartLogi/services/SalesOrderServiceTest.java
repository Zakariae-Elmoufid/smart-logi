package com.example.SmartLogi.services;


import com.example.SmartLogi.entities.Inventory;
import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.entities.SalesOrder;
import com.example.SmartLogi.entities.SalesOrderLine;
import com.example.SmartLogi.repositories.InventoryMovementRepository;
import com.example.SmartLogi.repositories.InventoryRepository;
import com.example.SmartLogi.repositories.SalesOrderRepository;
import com.example.SmartLogi.repositories.WarehouseRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.Timeout;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesOrderServiceTest {

    @Mock
    SalesOrderRepository salesOrderRepository;

    @Mock
    InventoryRepository inventoryRepository;
    @Mock
    WarehouseRepository warehouseRepository;
    @Mock
    InventoryMovementRepository inventoryMovementRepository;

    @InjectMocks
    SalesOrderService salesOrderService;


//    @Test
//    public void createBackOrder_whenStockIsNotSufficient() {
//        Product product = Product.builder()
//                .id(1L)
//                .name("Test Product")
//                .build();
//
//        SalesOrderLine line = SalesOrderLine.builder()
//                .id(1L)
//                .product(product)
//                .quantityRequested(50)
//                .build();
//
//        SalesOrder order = SalesOrder.builder()
//                .id(1L)
//                .orderLines(List.of(line))
//                .build();
//
//        // Spy the service
//        SalesOrderService spyService = Mockito.spy(salesOrderService);
//
//        // Mock the internal method to simulate insufficient stock
//        Mockito.doReturn(-1L)
//                .when(spyService)
//                .consolidateQuantityInOneWarehouse(50, 1L);
//
//        // Act
//        spyService.reserveLines(order);
//
//        // Assert
//        assertEquals(0, line.getQuantityReserved());
//        assertEquals(50, line.getQuantityBackorder());
//
//    }


}
