package com.example.SmartLogi.dto;

import com.example.SmartLogi.enums.OrderLineStatus;

public record SalesOrderLineResponseDTO(
        Long id,
        Long productId,
        String productName,
        Integer quantityRequested,
        Integer quantityBackorder,
        Integer quantityReserved,
        OrderLineStatus status,
        Double price
) {
}
