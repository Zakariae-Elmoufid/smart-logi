package com.example.SmartLogi.dto;

import com.example.SmartLogi.entities.Inventory;
import com.example.SmartLogi.enums.MovementType;

import java.time.LocalDateTime;

public record InventoryMovementResponseDTO (
        long id ,
        int quantity,
        long   inventoryId,
        MovementType movementType,
        LocalDateTime createdAt
){
}
