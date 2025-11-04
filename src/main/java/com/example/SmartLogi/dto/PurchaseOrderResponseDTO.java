package com.example.SmartLogi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderResponseDTO (
        long supplierId,
        long warehouseId,
        LocalDateTime expectedDate,
        List< PurchaseOrderLineRsponseDTO> purchaseOrderLineResponsetDTO
){
}
