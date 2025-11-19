package com.example.SmartLogi.services;


import com.example.SmartLogi.dto.InventoryMovementResponseDTO;
import com.example.SmartLogi.dto.PurchaseOrderRequestDTO;
import com.example.SmartLogi.dto.PurchaseOrderResponseDTO;
import com.example.SmartLogi.dto.ReceivePurchaseOrderRequestDTO;
import com.example.SmartLogi.entities.*;
import com.example.SmartLogi.enums.MovementType;
import com.example.SmartLogi.enums.PurchaseOrderStatus;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.InventoryMapper;
import com.example.SmartLogi.mapper.InventoryMovementMapper;
import com.example.SmartLogi.mapper.PurchaseOrderMapper;
import com.example.SmartLogi.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryMovementMapper  inventoryMovementMapper;

    public PurchaseOrderResponseDTO create(PurchaseOrderRequestDTO dto) {
         Supplier supplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + dto.supplierId()));

         Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + dto.warehouseId()));

        PurchaseOrder order = PurchaseOrder.builder()
                .supplier(supplier)
                .warehouse(warehouse)
                .orderStatus(PurchaseOrderStatus.CREATED)
                .expectedDate(dto.expectedDate())
                .orderDate(LocalDateTime.now())
                .build();

        List<PurchaseOrderLine> lines = dto.liens().stream().map(line -> {
           Product product= productRepository.findById(line.productId()).filter(Product::getActive)
                    .orElseThrow(()-> new ResourceNotFoundException("Product "+line.productId()+" not found"));
           return   PurchaseOrderLine.builder().product(product)
                    .unitPrice(product.getPurchasePrice())
                    .quantity(line.quantity())
                    .purchaseOrder(order)
                    .build();
        }).toList();
        order.setOrderLines(lines);
        return  purchaseOrderMapper.toDto(purchaseOrderRepository.save(order));
    }

   public List<PurchaseOrderResponseDTO> getAll(){
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
        return orders.stream().map(purchaseOrderMapper::toDto).toList();
   }

   public  PurchaseOrderResponseDTO getById(Long id){
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found"));
            return purchaseOrderMapper.toDto(purchaseOrder);
   }

    public  PurchaseOrderResponseDTO approvePurchaseOrder(long id){
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found"));

        order.setOrderStatus(PurchaseOrderStatus.APPROVED);
        purchaseOrderRepository.save(order);
        return purchaseOrderMapper.toDto(order);
    }

    @Transactional
    public InventoryMovementResponseDTO receiveProduct(Long purchaseOrderId, ReceivePurchaseOrderRequestDTO dto) {
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found"));

        PurchaseOrderLine line = po.getOrderLines().stream()
                .filter(l -> l.getProduct().getId().equals(dto.productId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in order"));

        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(line.getProduct().getId(),po.getWarehouse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        InventoryMovement inventoryMovement =  InventoryMovement.builder()
                .movementType(MovementType.INBOUND)
                .inventory(inventory)
                .quantity(dto.receivedQty())
                .createdAt(LocalDateTime.now())
                .build();
          inventoryMovementRepository.save(inventoryMovement);

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() + dto.receivedQty());
        inventoryRepository.save(inventory);

        int totalOrdered = po.getOrderLines().stream()
                .mapToInt(PurchaseOrderLine::getQuantity)
                .sum();

        int totalReceived = inventoryMovementRepository.getTotalReceivedQuantity(
                line.getProduct().getId(),
                po.getWarehouse().getId(),
                po.getId(),
                MovementType.INBOUND
        );
        po.setOrderStatus(totalReceived >= totalOrdered ? PurchaseOrderStatus.RECEIVED : PurchaseOrderStatus.PARTIALLY_RECEIVED);
        purchaseOrderRepository.save(po);
        return inventoryMovementMapper.toDTO(inventoryMovement);
    }




}
