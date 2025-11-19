package com.example.SmartLogi.dto;

import com.example.SmartLogi.enums.PurchaseOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderResponseDTO (
        long id,
        long supplierId,
        long warehouseId,
        String orderStatus,
        LocalDateTime expectedDate,
        LocalDateTime orderDate,
        List<PurchaseOrderLineRsponseDTO> lines
){
}
