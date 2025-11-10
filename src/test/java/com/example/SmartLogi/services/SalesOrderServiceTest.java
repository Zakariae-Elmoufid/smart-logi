package com.example.SmartLogi.services;


import com.example.SmartLogi.dto.SalesOrderLineRequestDTO;
import com.example.SmartLogi.dto.SalesOrderLineResponseDTO;
import com.example.SmartLogi.dto.SalesOrderRequestDTO;
import com.example.SmartLogi.dto.SalesOrderResponseDTO;
import com.example.SmartLogi.entities.Client;
import com.example.SmartLogi.entities.Inventory;
import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.enums.OrderLineStatus;
import com.example.SmartLogi.enums.OrderStatus;
import com.example.SmartLogi.mapper.SalesOrderMapper;
import com.example.SmartLogi.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesOrderServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private SalesOrderService salesOrderService;

    @Mock
    private SalesOrderMapper salesOrderMapper;



}
