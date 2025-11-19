package com.example.SmartLogi.mapper;


import com.example.SmartLogi.dto.WarehouseRequestDTO;
import com.example.SmartLogi.dto.WarehouseResponseDTO;
import com.example.SmartLogi.entities.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toEntity(WarehouseRequestDTO dto);
    WarehouseResponseDTO toDTO(Warehouse entity);
}
