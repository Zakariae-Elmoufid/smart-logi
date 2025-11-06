package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.SalesOrderRequestDTO;
import com.example.SmartLogi.dto.SalesOrderResponseDTO;
import com.example.SmartLogi.entities.*;
import com.example.SmartLogi.enums.OrderLineStatus;
import com.example.SmartLogi.enums.OrderStatus;
import com.example.SmartLogi.enums.ShipmentStatus;
import com.example.SmartLogi.exception.BusinessException;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.SalesOrderLineMapper;
import com.example.SmartLogi.mapper.SalesOrderMapper;
import com.example.SmartLogi.repositories.*;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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


    @Transactional
    public SalesOrderResponseDTO create(SalesOrderRequestDTO dto) {
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        SalesOrder order = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
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

        order = salesOrderRepository.save(order);

        String message = order.getOrderLines().stream()
                .map(line -> String.format("%s: %d/%d reserved, %d backorder",
                        line.getProduct().getName(),
                        line.getQuantityReserved(),
                        line.getQuantityRequested(),
                        line.getQuantityBackorder()))
                .collect(Collectors.joining("; "));

        SalesOrderResponseDTO mappedOrder = salesOrderMapper.toDTO(order);
        return new SalesOrderResponseDTO(
                mappedOrder.id(),
                mappedOrder.clientId(),
                mappedOrder.orderStatus(),
                mappedOrder.orderLines(),
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


}
