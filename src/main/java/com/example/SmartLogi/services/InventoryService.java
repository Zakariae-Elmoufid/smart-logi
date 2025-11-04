package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.InventoryMovementRequestDTO;
import com.example.SmartLogi.dto.InventoryMovementResponseDTO;
import com.example.SmartLogi.dto.InventoryRequestDTO;
import com.example.SmartLogi.dto.InventoryResponseDTO;
import com.example.SmartLogi.entities.*;
import com.example.SmartLogi.enums.MovementType;
import com.example.SmartLogi.exception.BusinessException;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.InventoryMapper;
import com.example.SmartLogi.mapper.InventoryMovementMapper;
import com.example.SmartLogi.repositories.InventoryMovementRepository;
import com.example.SmartLogi.repositories.InventoryRepository;
import com.example.SmartLogi.repositories.ProductRepository;
import com.example.SmartLogi.repositories.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryMapper mapper;
    private final InventoryMovementRepository movementRepository;
    private final InventoryMovementMapper inventoryMovementMapper;


   public InventoryResponseDTO create(InventoryRequestDTO dto) {
       Inventory inventory = mapper.toEntity(dto);

       Product product = productRepository.findById(dto.productId())
               .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
       Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
               .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));


       inventoryRepository.findByProductIdAndWarehouseId(dto.productId(), dto.warehouseId()).ifPresent(inv -> {
           throw new BusinessException("Inventory already exists for product " + dto.productId() +
                   " in warehouse " + dto.warehouseId());
       });

       inventory.setProduct(product);
       inventory.setWarehouse(warehouse);
       inventory.setQuantityOnHand(0);
       inventory.setQuantityReserved(0);

       return mapper.toDto(inventoryRepository.save(inventory));
   }


   public  InventoryResponseDTO getById(long id) {
      Inventory inventory = inventoryRepository.findById(id)
              .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
      return mapper.toDto(inventory);
   }

   public List<InventoryResponseDTO> getAll() {
       List<Inventory> inventory = inventoryRepository.findAll();
       return   inventory.stream().map(mapper::toDto).toList();
   }



    @Transactional
    public InventoryMovementResponseDTO recordInbound(InventoryMovementRequestDTO dto) {
        if(dto.quantity() <= 0){
            throw  new BusinessException("quantity  must be  greater than inventory quantity");
        }
        Inventory inventory = inventoryRepository.findById(dto.inventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() + dto.quantity());
        inventoryRepository.save(inventory);

        InventoryMovement inventoryMovement = InventoryMovement.builder().inventory(inventory)
                .movementType(MovementType.INBOUND)
                .quantity(dto.quantity())
                .createdAt(LocalDateTime.now())
                .build();
         return  inventoryMovementMapper.toDTO(movementRepository.save(inventoryMovement));
    }

    @Transactional
    public InventoryMovementResponseDTO recordOutbound(InventoryMovementRequestDTO dto) {
       Inventory inventory = inventoryRepository.findById(dto.inventoryId())
               .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
       if(dto.quantity() <= 0){
           throw  new BusinessException("quantity  must be  greater than inventory quantity");
       }

       if(inventory.getQuantityOnHand() < dto.quantity()){
          throw  new BusinessException("quantity is greater than inventory quantity");
       }

       inventory.setQuantityOnHand(inventory.getQuantityOnHand() - dto.quantity());
       inventoryRepository.save(inventory);


       InventoryMovement inventoryMovement = InventoryMovement.builder()
               .inventory(inventory)
               .movementType(MovementType.OUTBOUND)
               .quantity(dto.quantity())
               .createdAt(LocalDateTime.now())
               .build();
       return  inventoryMovementMapper.toDTO(movementRepository.save(inventoryMovement));
    }

    @Transactional
    public InventoryMovementResponseDTO recordAdjustment(InventoryMovementRequestDTO dto) {
        Inventory inventory = inventoryRepository.findById(dto.inventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        int qtyOnHand = inventory.getQuantityOnHand();
        int qtyReserved = inventory.getQuantityReserved();
        int adjustmentQty = dto.quantity();

        if (adjustmentQty < 0 && (qtyOnHand + adjustmentQty) < qtyReserved) {
            throw new BusinessException("Adjustment refused: qtyOnHand cannot be less than qtyReserved.");
        }

        inventory.setQuantityOnHand(qtyOnHand + adjustmentQty);
        inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .movementType(MovementType.ADJUSTMENT)
                .quantity(adjustmentQty)
                .createdAt(LocalDateTime.now())
                .build();
        movementRepository.save(movement);
        return inventoryMovementMapper.toDTO(movement);
    }


    public void  releaseInventory(SalesOrder order){
        order.getOrderLines().forEach(line -> {
            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouseId(line.getProduct().getId(), order.getWarehouse().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product " + line.getProduct().getName()));

            inventory.setQuantityReserved(
                    Math.max(0, inventory.getQuantityReserved() -
                            (line.getQuantityReserved() != null ? line.getQuantityReserved() : 0))
            );

            inventoryRepository.save(inventory);
        });
    }

}
