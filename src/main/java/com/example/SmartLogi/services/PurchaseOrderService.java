package com.example.SmartLogi.services;


import com.example.SmartLogi.dto.PurchaseOrderRequestDTO;
import com.example.SmartLogi.dto.PurchaseOrderResponseDTO;
import com.example.SmartLogi.entities.PurchaseOrder;
import com.example.SmartLogi.entities.Supplier;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.enums.PurchaseOrderStatus;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.PurchaseOrderMapper;
import com.example.SmartLogi.repositories.ProductRepository;
import com.example.SmartLogi.repositories.PurchaseOrderRepository;
import com.example.SmartLogi.repositories.SupplierRepository;
import com.example.SmartLogi.repositories.WarehouseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderResponseDTO create(PurchaseOrderRequestDTO dto) {
         Supplier supplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + dto.supplierId()));

         Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + dto.warehouseId()));
        PurchaseOrder order = purchaseOrderMapper.toEntity(dto);
        order.setSupplier(supplier);
        order.setWarehouse(warehouse);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(PurchaseOrderStatus.CREATED);

        dto.liens().forEach(line -> {
            productRepository.findById(line.productId())
                    .orElseThrow(()-> new ResourceNotFoundException("Product "+line.productId()+" not found")
            );
        });
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




}
