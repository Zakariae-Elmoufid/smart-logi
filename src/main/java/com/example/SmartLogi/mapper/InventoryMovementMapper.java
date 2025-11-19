package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.InventoryMovementRequestDTO;
import com.example.SmartLogi.dto.InventoryMovementResponseDTO;
import com.example.SmartLogi.entities.InventoryMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface InventoryMovementMapper {
    public  InventoryMovement toEntity(InventoryMovementRequestDTO dto);
    @Mapping(target = "inventoryId", source = "inventory.id")
    InventoryMovementResponseDTO toDTO(InventoryMovement entity);
}
