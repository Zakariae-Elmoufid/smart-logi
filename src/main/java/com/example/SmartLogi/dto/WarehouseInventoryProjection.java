package com.example.SmartLogi.dto;

public record WarehouseInventoryProjection(Long warehouseId,
                                           Long inventoryId,
                                           Integer quantityHand) {
}
