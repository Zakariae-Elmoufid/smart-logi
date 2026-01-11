package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.SalesOrderLineRequestDTO;
import com.example.SmartLogi.dto.SalesOrderRequestDTO;
import com.example.SmartLogi.dto.SalesOrderResponseDTO;
import com.example.SmartLogi.entities.*;
import com.example.SmartLogi.enums.OrderStatus;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.SalesOrderLineMapper;
import com.example.SmartLogi.mapper.SalesOrderMapper;
import com.example.SmartLogi.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private SalesOrderMapper salesOrderMapper;

    @Mock
    private SalesOrderLineMapper salesOrderLineMapper;

    @InjectMocks
    private SalesOrderService salesOrderService;

    private SalesOrder salesOrder;
    private Client client;
    private Product product;
    private Warehouse warehouse;
    private SalesOrderResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(UserRole.CLIENT)
                .enabled(true)
                .build();

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .sku("SKU-001")
                .purchasePrice(100.0)
                .sellingPrice(150.0)
                .active(true)
                .build();

        salesOrder = SalesOrder.builder()
                .id(1L)
                .client(client)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        responseDTO = new SalesOrderResponseDTO(
                1L, 1L, OrderStatus.CREATED, List.of(), "Order created"
        );
    }

    @Test
    void create_ShouldReturnSalesOrderResponseDTO_WhenClientFound() {
        SalesOrderLineRequestDTO lineDTO = new SalesOrderLineRequestDTO(1L, 5);
        SalesOrderRequestDTO requestDTO = new SalesOrderRequestDTO(1L, List.of(lineDTO));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findWarehousesByProductId(anyLong())).thenReturn(List.of());
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
        when(salesOrderMapper.toDTO(any(SalesOrder.class))).thenReturn(responseDTO);

        SalesOrderResponseDTO result = salesOrderService.create(requestDTO);

        assertNotNull(result);
        verify(clientRepository, times(1)).findById(1L);
        verify(salesOrderRepository, times(1)).save(any(SalesOrder.class));
    }

    @Test
    void create_ShouldThrowException_WhenClientNotFound() {
        SalesOrderLineRequestDTO lineDTO = new SalesOrderLineRequestDTO(1L, 5);
        SalesOrderRequestDTO requestDTO = new SalesOrderRequestDTO(99L, List.of(lineDTO));

        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> salesOrderService.create(requestDTO));
    }

    @Test
    void create_ShouldThrowException_WhenProductNotFound() {
        SalesOrderLineRequestDTO lineDTO = new SalesOrderLineRequestDTO(99L, 5);
        SalesOrderRequestDTO requestDTO = new SalesOrderRequestDTO(1L, List.of(lineDTO));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> salesOrderService.create(requestDTO));
    }

    @Test
    void consolidateQuantityInOneWarehouse_ShouldReturnMinusOne_WhenNoStock() {
        when(warehouseRepository.findWarehousesByProductId(1L)).thenReturn(List.of());

        long result = salesOrderService.consolidateQuantityInOneWarehouse(10, 1L);

        assertEquals(-1L, result);
    }
}
