package com.example.SmartLogi.mapper;

import com.example.SmartLogi.controllers.PurchaseOrderController;
import com.example.SmartLogi.dto.PurchaseOrderRequestDTO;
import com.example.SmartLogi.dto.PurchaseOrderResponseDTO;
import com.example.SmartLogi.entities.PurchaseOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
    PurchaseOrderResponseDTO toDto(PurchaseOrder purchaseOrder);
    PurchaseOrder toEntity(PurchaseOrderRequestDTO purchaseOrderRequestDTO);
}
