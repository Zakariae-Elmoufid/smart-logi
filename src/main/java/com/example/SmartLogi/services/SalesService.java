//package com.example.SmartLogi.services;
//
//
//import com.example.SmartLogi.dto.SalesOrderRequestDTO;
//import com.example.SmartLogi.dto.SalesOrderResponseDTO;
//import com.example.SmartLogi.dto.WarehouseInventoryProjection;
//import com.example.SmartLogi.entities.*;
//import com.example.SmartLogi.enums.MovementType;
//import com.example.SmartLogi.enums.OrderLineStatus;
//import com.example.SmartLogi.enums.OrderStatus;
//import com.example.SmartLogi.exception.ResourceNotFoundException;
//import com.example.SmartLogi.mapper.SalesOrderLineMapper;
//import com.example.SmartLogi.mapper.SalesOrderMapper;
//import com.example.SmartLogi.repositories.*;
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Comparator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//public class SalesService {
//
//    @Autowired
//    private SalesOrderRepository salesOrderRepository;
//    @Autowired
//
//    private ClientRepository clientRepository;
//    @Autowired
//
//    private WarehouseRepository warehouseRepository;
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private InventoryRepository inventoryRepository;
//    @Autowired
//    private  InventoryMovementRepository inventoryMovementRepository;
//    @Autowired
//    private SalesOrderMapper salesOrderMapper;
//    @Autowired
//    private SalesOrderLineRepository salesOrderLineRepository;
//    @Autowired
//    private ShipmentRepository shipmentRepository;
//
//    @Autowired
//    private SalesOrderMapper salesOrderMapper;
//    @Autowired
//    private SalesOrderLineMapper salesOrderLineMapper;
//
//    @Transactional
//    public SalesOrderResponseDTO create(SalesOrderRequestDTO dto) {
//        Client client = clientRepository.findById(dto.clientId())
//                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
//
//
//        SalesOrder order = SalesOrder.builder()
//                .client(client)
//                .orderStatus(OrderStatus.CREATED)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        SalesOrder tempOrder = order;
//
//        List<SalesOrderLine> orderLines = dto.lines().stream()
//                .map(lineDto -> {
//                    Product product = productRepository.findById(lineDto.productId())
//                            .filter(Product::getActive)
//                            .orElseThrow(() -> new ResourceNotFoundException(
//                                    "Product not found or inactive, id: " + lineDto.productId()));
//
//                    return SalesOrderLine.builder()
//                            .product(product)
//                            .price(product.getSellingPrice())
//                            .quantityRequested(lineDto.quantityRequested())
//                            .salesOrder(tempOrder)
//                            .build();
//                })
//                .toList();
//
//        order.setOrderLines(orderLines);
//
//        reserveLines(order);
//
//        order = salesOrderRepository.save(order);
//
//        String message = order.getOrderLines().stream()
//                .map(line -> String.format("%s: %d/%d reserved, %d backorder",
//                        line.getProduct().getName(),
//                        line.getQuantityReserved(),
//                        line.getQuantityRequested(),
//                        line.getQuantityBackorder()))
//                .collect(Collectors.joining("; "));
//
//        SalesOrderResponseDTO mappedOrder = salesOrderMapper.toDTO(order);
//        return new SalesOrderResponseDTO(
//                mappedOrder.id(),
//                mappedOrder.clientId(),
//                mappedOrder.orderStatus(),
//                mappedOrder.orderLines(),
//                message
//        );
//    }
//
//    @Transactional
//    public void  reserveLines(SalesOrder order) {
//        order.getOrderLines().forEach(line -> {
//            long warehouseId = consolidateQuantityInOneWarehouse(line.getQuantityRequested() , line.getProduct().getId());
//            Inventory inventory = inventoryRepository
//                    .findByProductIdAndWarehouseId(line.getProduct().getId(), warehouseId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product " + line.getProduct().getName()));
//            int available = inventory.getQuantityOnHand() - inventory.getQuantityReserved();
//            if (available >= line.getQuantityRequested()) {
//                inventory.setQuantityReserved(inventory.getQuantityReserved() + line.getQuantityRequested());
//                line.setQuantityReserved(line.getQuantityRequested());
//                line.setQuantityBackorder(0);
//                line.setStatus(OrderLineStatus.RESERVED);
//            } else if (available > 0) {
//                inventory.setQuantityReserved(inventory.getQuantityReserved() + available);
//                line.setQuantityReserved(available);
//                line.setQuantityBackorder(line.getQuantityRequested() - available);
//                line.setStatus(OrderLineStatus.PARTIALLY_RESERVED);
//            } else {
//                line.setQuantityReserved(0);
//                line.setQuantityBackorder(line.getQuantityRequested());
//                line.setStatus(OrderLineStatus.NOT_RESERVED);
//            }
//            if (order.getWarehouse() == null) {
//                Warehouse warehouse = warehouseRepository.findById(warehouseId)
//                        .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
//                order.setWarehouse(warehouse);
//            }
//            inventoryRepository.save(inventory);
//
//        }
//    }
//
//
//    @Transactional
//    public long consolidateQuantityInOneWarehouse(int quantityRequested, long productId) {
//
//        List<WarehouseInventoryProjection> warehouses = warehouseRepository.findWarehousesByProductId(productId);
//
//        int totalAvailable = warehouses.stream()
//                .mapToInt(WarehouseInventoryProjection::quantityHand)
//                .sum();
//
//        //  Check if any warehouse can handle all
//        for (WarehouseInventoryProjection w : warehouses) {
//            if (w.quantityHand() >= quantityRequested) {
//                return w.warehouseId();
//            }
//        }
//
//        if (totalAvailable < quantityRequested) {
//            return -1L;
//        }
//
//        //  Select main warehouse
//        WarehouseInventoryProjection mainWarehouse = warehouses.stream()
//                .max(Comparator.comparingInt(WarehouseInventoryProjection::quantityHand))
//                .orElseThrow();
//
//        Inventory mainInventory = inventoryRepository.findById(mainWarehouse.inventoryId()).orElseThrow();
//
//        int remainingToTransfer = quantityRequested - mainWarehouse.quantityHand();
//
//        if (remainingToTransfer > 0) {
//            for (WarehouseInventoryProjection w : warehouses) {
//                if (w.warehouseId() == mainWarehouse.warehouseId()) continue;
//
//                int transferable = Math.min(w.quantityHand(), remainingToTransfer);
//                Inventory inventory = inventoryRepository.findById(w.inventoryId()).orElseThrow();
//
//                mainInventory.setQuantityOnHand(mainInventory.getQuantityOnHand() + transferable);
//                InventoryMovement Inbound =  InventoryMovement.builder()
//                                .quantity(transferable).inventory(mainInventory)
//                                .movementType(MovementType.INBOUND)
//                                        .description("receive product from warehouse "+mainWarehouse.warehouseId()+" for consolidate Quantity In this warehouse ")
//                                                .createdAt(LocalDateTime.now()).build();
//                inventoryMovementRepository.save(Inbound);
//
//                inventory.setQuantityOnHand(inventory.getQuantityOnHand() - transferable);
//                InventoryMovement outbound = InventoryMovement.builder()
//                                .quantity(transferable)
//                                        .inventory(inventory)
//                        .movementType(MovementType.OUTBOUND)
//                        .createdAt(LocalDateTime.now())
//                        .description("tansfer product form this warehouse to "+ w.warehouseId() +" for consolidate Quantity In this warehouse "+mainWarehouse.warehouseId())
//                        .build();
//
//                inventoryRepository.save(inventory);
//                remainingToTransfer -= transferable;
//
//                if (remainingToTransfer <= 0) break;
//            }
//        }
//
//        inventoryRepository.save(mainInventory);
//        return mainWarehouse.warehouseId();
//    }
//
//}
