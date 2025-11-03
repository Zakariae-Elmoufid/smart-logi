package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.SalesOrderRequestDTO;
import com.example.SmartLogi.dto.SalesOrderResponseDTO;
import com.example.SmartLogi.entities.*;
import com.example.SmartLogi.enums.OrderLineStatus;
import com.example.SmartLogi.enums.OrderStatus;
import com.example.SmartLogi.enums.ShipmentStatus;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.SalesOrderLineMapper;
import com.example.SmartLogi.mappers.SalesOrderMapper;
import com.example.SmartLogi.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  com.example.SmartLogi.enums.OrderLineStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private SalesOrderMapper salesOrderMapper;
    @Autowired
    private SalesOrderLineMapper  salesOrderLineMapper;


    public SalesOrderResponseDTO create(SalesOrderRequestDTO dto) {
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        SalesOrder  order = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();
//        salesOrderRepository.save(order);

        List<SalesOrderLine> lines = dto.lines().stream().map(lineDto -> {
            Product product = productRepository.findById(lineDto.productId())
                    .filter(Product::getActive)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found or inactive, id: " + lineDto.productId()));
            return  SalesOrderLine.builder().product(product)
                    .price(product.getSellingPrice())
                    .quantityRequested(lineDto.quantityRequested())
                    .salesOrder(order)
                    .build();
        }).toList();



        order.setOrderLines(lines);
        reserveLines(order);

        String message = order.getOrderLines().stream()
                .map(line -> line.getProduct().getName() + ": " +
                        line.getQuantityReserved() + "/" + line.getQuantityRequested() +
                        " reserved, " + line.getQuantityBackorder() + " backorder")
                .reduce((l1,l2) -> l1 + "; " + l2)
                .orElse("");

        SalesOrderResponseDTO response = salesOrderMapper.toDTO(order);
        return new SalesOrderResponseDTO(
                response.id(),
                response.clientId(),
                response.orderStatus(),
                response.orderLines(),
                message
        );
    }

    private void reserveLines(SalesOrder order) {
        order.getOrderLines().forEach(line -> {
            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouseId(line.getProduct().getId(), order.getWarehouse().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product " + line.getProduct().getName()));

            int available = inventory.getQuantityOnHand() - inventory.getQuantityReserved();
            if (available >= line.getQuantityRequested()) {
                inventory.setQuantityReserved(inventory.getQuantityReserved() + line.getQuantityRequested());
                line.setQuantityReserved(line.getQuantityRequested());
                line.setQuantityBackorder(0);
                line.setStatus(OrderLineStatus.RESERVED);
            } else if (available > 0) {
                inventory.setQuantityReserved(inventory.getQuantityReserved() + available);
                line.setQuantityReserved(available);
                line.setQuantityBackorder(line.getQuantityRequested() - available);
                line.setStatus(OrderLineStatus.PARTIALLY_RESERVED);
            } else {
                line.setQuantityReserved(0);
                line.setQuantityBackorder(line.getQuantityRequested());
                line.setStatus(OrderLineStatus.NOT_RESERVED);
            }

            inventoryRepository.save(inventory);
        });



        if (order.getOrderLines().stream().allMatch(l -> l.getStatus() == OrderLineStatus.RESERVED)) {
            order.setOrderStatus(OrderStatus.RESERVED);
        } else if (order.getOrderLines().stream().anyMatch(l -> l.getQuantityReserved() > 0)) {
            order.setOrderStatus(OrderStatus.PARTIALLY_RESERVED);
        } else {
            order.setOrderStatus(OrderStatus.CREATED);
        }
        salesOrderRepository.save(order);
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
                .trackingNumber(UUID.randomUUID().toString())
                .shipmentStatus(ShipmentStatus.PLANNED)
                .plannedDate(LocalDateTime.now())
                .build();

        shipmentRepository.save(shipment);

        return salesOrderMapper.toDTO(order);
    }


}
