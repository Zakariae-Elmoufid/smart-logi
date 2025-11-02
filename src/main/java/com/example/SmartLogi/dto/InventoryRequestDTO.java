package com.example.SmartLogi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRequestDTO(

//        @NotNull(message = "qtyOnHand must be provided")
//        @PositiveOrZero(message = "qtyOnHand must be >= 0")
//        int quantityOnHand,
//        @PositiveOrZero(message = "qtyReserved must be >= 0")
//        @NotNull(message = "qtyReserved must be provided")
//        int quantityReserved,

        @NotNull(message = "productId must be provided")
         long productId,

         @NotNull(message = "warehouseId must be provided")
         long warehouseId
) {
}
