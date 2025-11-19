package com.example.SmartLogi.dto;

import com.example.SmartLogi.entities.Product;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;

public record PurchaseOrderLineRequestDTO(
        @Positive(message = "Quantity must be greater than 0")
        int quantity,

        @Positive(message = "Product ID must be positive")
        long productId
) {
}
