package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.PurchaseOrderRequestDTO;
import com.example.SmartLogi.dto.PurchaseOrderResponseDTO;
import com.example.SmartLogi.entities.PurchaseOrder;
import com.example.SmartLogi.entities.PurchaseOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring" ,  uses = PurchaseOrderLineMapper.class)
public interface PurchaseOrderMapper {
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "warehouseId",source = "warehouse.id")
    PurchaseOrderResponseDTO toDto(PurchaseOrder purchaseOrder);
    PurchaseOrder toEntity(PurchaseOrderRequestDTO purchaseOrderRequestDTO);
}
