package com.example.SmartLogi.dto;

import jakarta.validation.constraints.Positive;



public record PurchaseOrderLineRsponseDTO (
        int quantity,
        double unitPrice,
        long productId,
        String productName
) {
}