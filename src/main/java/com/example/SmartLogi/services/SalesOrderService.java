package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.SalesOrderRequestDTO;
import com.example.SmartLogi.dto.SalesOrderResponseDTO;
import com.example.SmartLogi.dto.WarehouseInventoryProjection;
import com.example.SmartLogi.entities.*;
import com.example.SmartLogi.entities.SalesOrder;

import com.example.SmartLogi.enums.MovementType;
import com.example.SmartLogi.enums.OrderLineStatus;
import com.example.SmartLogi.enums.OrderStatus;
import com.example.SmartLogi.enums.ShipmentStatus;
import com.example.SmartLogi.exception.BusinessException;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.SalesOrderLineMapper;
import com.example.SmartLogi.mapper.SalesOrderMapper;
import com.example.SmartLogi.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.SmartLogi.enums.OrderLineStatus.PARTIALLY_RESERVED;

@Service
public class SalesOrderService {


    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired

    private ClientRepository clientRepository;
    @Autowired

    private WarehouseRepository warehouseRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private SalesOrderLineRepository salesOrderLineRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private  InventoryMovementRepository inventoryMovementRepository;
    @Autowired
    private InventoryService inventoryService;
    private SalesOrderService salesOrderService;

    @Autowired
    private SalesOrderMapper salesOrderMapper;
    @Autowired
    private SalesOrderLineMapper  salesOrderLineMapper;



    @Transactional
    public SalesOrderResponseDTO create(SalesOrderRequestDTO dto) {
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));


        SalesOrder order = SalesOrder.builder()
                .client(client)
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        SalesOrder tempOrder = order;

        List<SalesOrderLine> orderLines = dto.lines().stream()
                .map(lineDto -> {
                    Product product = productRepository.findById(lineDto.productId())
                            .filter(Product::getActive)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Product not found or inactive, id: " + lineDto.productId()));

                    return SalesOrderLine.builder()
                            .product(product)
                            .price(product.getSellingPrice())
                            .quantityRequested(lineDto.quantityRequested())
                            .salesOrder(tempOrder)
                            .build();
                })
                .toList();

        order.setOrderLines(orderLines);
        reserveLines(order);


        String message = order.getOrderLines().stream()
                .map(line -> String.format("%s: %d/%d reserved, %d backorder",
                        line.getProduct().getName(),
                        line.getQuantityReserved(),
                        line.getQuantityRequested(),
                        line.getQuantityBackorder()))
                .collect(Collectors.joining("; "));

        SalesOrderResponseDTO mappedOrder = salesOrderMapper.toDTO(salesOrderRepository.save(order));
        return new SalesOrderResponseDTO(
                mappedOrder.id(),
                mappedOrder.clientId(),
                mappedOrder.orderStatus(),
                mappedOrder.orderLines(),
                message
        );
    }

    @Transactional
    public void reserveLines(SalesOrder order) {

        for (SalesOrderLine line : order.getOrderLines()) {
            long warehouseId = consolidateQuantityInOneWarehouse(line.getQuantityRequested(), line.getProduct().getId());

            if (warehouseId == -1L) {
                // Not enough stock → backorder fully
                line.setQuantityReserved(0);
                line.setQuantityBackorder(line.getQuantityRequested());
                line.setStatus(OrderLineStatus.NOT_RESERVED);
                continue;
            }


              inventoryService.reservedQuantity(line, warehouseId);


            // Set warehouse only if not set yet
            if (order.getWarehouse() == null) {
                Warehouse warehouse = warehouseRepository.findById(warehouseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
                order.setWarehouse(warehouse);
            }
        }

        // Update order status based on lines
        if (order.getOrderLines().stream().allMatch(l -> l.getStatus() == OrderLineStatus.RESERVED)) {
            order.setOrderStatus(OrderStatus.RESERVED);
            order.setReservedAt(LocalDateTime.now());
        } else if (order.getOrderLines().stream().anyMatch(l -> l.getQuantityReserved() > 0)) {
            order.setOrderStatus(OrderStatus.PARTIALLY_RESERVED);
        } else {
            order.setOrderStatus(OrderStatus.CREATED);
        }
    }


    @Transactional
    public long consolidateQuantityInOneWarehouse(int quantityRequested, long productId) {
        List<WarehouseInventoryProjection> warehouses = warehouseRepository.findWarehousesByProductId(productId);

        int totalAvailable = warehouses.stream()
                .mapToInt(WarehouseInventoryProjection::quantityHand)
                .sum();
            System.out.println("totalAvailable :" +  totalAvailable);

        if (totalAvailable < quantityRequested || totalAvailable == 0) {
            System.out.println("totalAvailable = " + totalAvailable + ", quantityRequested = " + quantityRequested);

            return -1L;
        }
        for (WarehouseInventoryProjection w : warehouses) {
            if (w.quantityHand() >= totalAvailable) {
                System.out.println("warehouse Id that has all qauntity  : "+w.warehouseId());
                System.out.println("warehouse  that has all qauntity  : "+ w.quantityHand());
                return w.warehouseId();
            }
        }




        //  Select main warehouse
        WarehouseInventoryProjection mainWarehouse = warehouses.stream()
                .max(Comparator.comparingInt(WarehouseInventoryProjection::quantityHand))
                .orElseThrow();
        System.out.println("the main warhouse :" + mainWarehouse.warehouseId());

        Inventory mainInventory = inventoryRepository.findById(mainWarehouse.inventoryId()).orElseThrow();
       System.out.println("the main inventory :"+ mainInventory.getId() );
        int remainingToTransfer = quantityRequested - mainWarehouse.quantityHand();
        System.out.println("remainingToTransfer :" + remainingToTransfer);
        if (remainingToTransfer > 0) {
            for (WarehouseInventoryProjection w : warehouses) {
                if (Objects.equals(w.warehouseId(), mainWarehouse.warehouseId())) continue;

                int transferable = Math.min(w.quantityHand(), remainingToTransfer);
                Inventory inventory = inventoryRepository.findById(w.inventoryId()).orElseThrow();

                mainInventory.setQuantityOnHand(mainInventory.getQuantityOnHand() + transferable);
                InventoryMovement Inbound =  InventoryMovement.builder()
                        .quantity(transferable).inventory(mainInventory)
                        .movementType(MovementType.INBOUND)
                        .description("receive product from warehouse "+mainWarehouse.warehouseId()+" for consolidate Quantity In this warehouse ")
                        .createdAt(LocalDateTime.now()).build();
                inventoryMovementRepository.save(Inbound);

                inventory.setQuantityOnHand(inventory.getQuantityOnHand() - transferable);
                InventoryMovement outbound = InventoryMovement.builder()
                        .quantity(transferable)
                        .inventory(inventory)
                        .movementType(MovementType.OUTBOUND)
                        .createdAt(LocalDateTime.now())
                        .description("transfer product form this warehouse to "+ w.warehouseId() +" for consolidate Quantity In this warehouse "+mainWarehouse.warehouseId())
                        .build();
                inventoryMovementRepository.save(outbound);
                inventoryRepository.save(inventory);
                remainingToTransfer -= transferable;

                if (remainingToTransfer <= 0) break;
            }
        }

        inventoryRepository.save(mainInventory);
        return mainWarehouse.warehouseId();
    }

    public SalesOrderResponseDTO confirmOrder(Long orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.RESERVED &&
                order.getOrderStatus() != OrderStatus.PARTIALLY_RESERVED) {
            throw new IllegalStateException("Order cannot be confirmed at this stage");
        }

        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());
        salesOrderRepository.save(order);
        Shipment shipment = Shipment.builder()
                .salesOrder(order)
                .trackingNumber("TRK-" + order.getId())
                .shipmentStatus(ShipmentStatus.PLANNED)
                .plannedDate(LocalDateTime.now())
                .build();

        shipmentRepository.save(shipment);

        return salesOrderMapper.toDTO(order);
    }


    public SalesOrderResponseDTO cancel(long id)  {
         SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.SHIPPED) {
            throw new BusinessException("Cannot cancel a shipped order. Use return process instead.");
        }

         if(order.getOrderStatus() == OrderStatus.CREATED) {
             order.setOrderStatus(OrderStatus.CANCELLED);

         }
        else if (order.getOrderStatus() == OrderStatus.PARTIALLY_RESERVED
                || order.getOrderStatus() == OrderStatus.RESERVED) {

             order.getOrderLines().forEach(line -> {
                System.out.println("Updating inventory for product: " + line.getProduct().getId());

                Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(
                                line.getProduct().getId(), order.getWarehouse().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));


                int reservedQty = line.getQuantityReserved() != null ? line.getQuantityReserved() : 0;
                int currentReserved = inventory.getQuantityReserved() != null ? inventory.getQuantityReserved() : 0;


                inventory.setQuantityReserved(Math.max(0, currentReserved - reservedQty));
                inventoryRepository.save(inventory);
            });

            order.setOrderStatus(OrderStatus.CANCELLED);
        }
         return salesOrderMapper.toDTO(salesOrderRepository.save(order));
    }

    @Transactional
    public void reservedBackOrder(long warehouseId, List<Long> productIds) {

        List<SalesOrder> backOrders =
                salesOrderRepository.findByWarehouse_IdAndOrderStatus(
                        warehouseId,
                        OrderStatus.PARTIALLY_RESERVED
                );

        for (SalesOrder order : backOrders) {

            for (SalesOrderLine line : order.getOrderLines()) {

                // Skip lines whose product is not in the received list
                if (!productIds.contains(line.getProduct().getId())) continue;

                Inventory inventory = inventoryRepository
                        .findByProductIdAndWarehouseId(line.getProduct().getId(), warehouseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

                int available = inventory.getQuantityOnHand() - inventory.getQuantityReserved();

                if (available >= line.getQuantityBackorder()) {

                    inventory.setQuantityReserved(
                            inventory.getQuantityReserved() + line.getQuantityBackorder()
                    );

                    line.setQuantityBackorder(0);
                    line.setStatus(OrderLineStatus.RESERVED);

                    inventoryRepository.save(inventory);
                }
            }

            boolean allReserved = order.getOrderLines()
                    .stream()
                    .allMatch(l -> l.getStatus() == OrderLineStatus.RESERVED);

            if (allReserved) {
                order.setOrderStatus(OrderStatus.RESERVED);
            } else {
                order.setOrderStatus(OrderStatus.PARTIALLY_RESERVED);
            }

            salesOrderRepository.save(order);
        }
    }


}
