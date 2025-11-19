package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.InventoryRequestDTO;
import com.example.SmartLogi.dto.InventoryResponseDTO;
import com.example.SmartLogi.entities.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring", uses = {ProductMapper.class, WarehouseMapper.class})

public interface InventoryMapper {
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    InventoryResponseDTO toDto(Inventory inventory);


    @Mapping(target = "product", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    Inventory toEntity(InventoryRequestDTO dto);

}
