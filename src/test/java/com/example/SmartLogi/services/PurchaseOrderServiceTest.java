package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.PurchaseOrderRequestDTO;
import com.example.SmartLogi.dto.PurchaseOrderResponseDTO;
import com.example.SmartLogi.dto.PurchaseOrderLineRequestDTO;
import com.example.SmartLogi.entities.*;
import com.example.SmartLogi.enums.PurchaseOrderStatus;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.InventoryMovementMapper;
import com.example.SmartLogi.mapper.PurchaseOrderMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private InventoryMovementMapper inventoryMovementMapper;

    @Mock
    private SalesOrderService salesOrderService;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private PurchaseOrder purchaseOrder;
    private Supplier supplier;
    private Warehouse warehouse;
    private Product product;
    private PurchaseOrderResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .supplierName("Test Supplier")
                .phoneNumber("0612345678")
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

        purchaseOrder = PurchaseOrder.builder()
                .id(1L)
                .supplier(supplier)
                .warehouse(warehouse)
                .orderStatus(PurchaseOrderStatus.CREATED)
                .orderDate(LocalDateTime.now())
                .expectedDate(LocalDateTime.now().plusDays(7))
                .build();

        // PurchaseOrderResponseDTO: id, supplierId, warehouseId, orderStatus, expectedDate, orderDate, lines
        responseDTO = new PurchaseOrderResponseDTO(
                1L, 1L, 1L,
                "CREATED", LocalDateTime.now().plusDays(7), LocalDateTime.now(), List.of()
        );
    }

    @Test
    void create_ShouldReturnPurchaseOrderResponseDTO() {
        // PurchaseOrderLineRequestDTO: quantity, productId
        PurchaseOrderLineRequestDTO lineDTO = new PurchaseOrderLineRequestDTO(10, 1L);
        PurchaseOrderRequestDTO requestDTO = new PurchaseOrderRequestDTO(
                1L, 1L, LocalDateTime.now().plusDays(7), List.of(lineDTO)
        );

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);
        when(purchaseOrderMapper.toDto(any(PurchaseOrder.class))).thenReturn(responseDTO);

        PurchaseOrderResponseDTO result = purchaseOrderService.create(requestDTO);

        assertNotNull(result);
        assertEquals("CREATED", result.orderStatus());
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
    }

    @Test
    void create_ShouldThrowException_WhenSupplierNotFound() {
        PurchaseOrderLineRequestDTO lineDTO = new PurchaseOrderLineRequestDTO(10, 1L);
        PurchaseOrderRequestDTO requestDTO = new PurchaseOrderRequestDTO(
                99L, 1L, LocalDateTime.now().plusDays(7), List.of(lineDTO)
        );

        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> purchaseOrderService.create(requestDTO));
    }

    @Test
    void create_ShouldThrowException_WhenWarehouseNotFound() {
        PurchaseOrderLineRequestDTO lineDTO = new PurchaseOrderLineRequestDTO(10, 1L);
        PurchaseOrderRequestDTO requestDTO = new PurchaseOrderRequestDTO(
                1L, 99L, LocalDateTime.now().plusDays(7), List.of(lineDTO)
        );

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> purchaseOrderService.create(requestDTO));
    }

    @Test
    void getAll_ShouldReturnListOfPurchaseOrders() {
        when(purchaseOrderRepository.findAll()).thenReturn(List.of(purchaseOrder));
        when(purchaseOrderMapper.toDto(purchaseOrder)).thenReturn(responseDTO);

        List<PurchaseOrderResponseDTO> result = purchaseOrderService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getById_ShouldReturnPurchaseOrder_WhenFound() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderMapper.toDto(purchaseOrder)).thenReturn(responseDTO);

        PurchaseOrderResponseDTO result = purchaseOrderService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        when(purchaseOrderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> purchaseOrderService.getById(99L));
    }

    @Test
    void approvePurchaseOrder_ShouldUpdateStatusToApproved() {
        PurchaseOrderResponseDTO approvedResponse = new PurchaseOrderResponseDTO(
                1L, 1L, 1L,
                "APPROVED", LocalDateTime.now().plusDays(7), LocalDateTime.now(), List.of()
        );

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderRepository.save(purchaseOrder)).thenReturn(purchaseOrder);
        when(purchaseOrderMapper.toDto(purchaseOrder)).thenReturn(approvedResponse);

        PurchaseOrderResponseDTO result = purchaseOrderService.approvePurchaseOrder(1L);

        assertNotNull(result);
        assertEquals("APPROVED", result.orderStatus());
        assertEquals(PurchaseOrderStatus.APPROVED, purchaseOrder.getOrderStatus());
    }

    @Test
    void approvePurchaseOrder_ShouldThrowException_WhenNotFound() {
        when(purchaseOrderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> purchaseOrderService.approvePurchaseOrder(99L));
    }
}
