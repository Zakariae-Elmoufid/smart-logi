package com.example.SmartLogi.dto;

import jakarta.validation.constraints.Positive;

public record ReceivePurchaseOrderRequestDTO(
        @Positive(message = "product ID must be positive")
        long productId,
        @Positive(message = "quantity  must be positive")
        int receivedQty
) {
}
