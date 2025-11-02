package com.example.SmartLogi.mapper;


import com.example.SmartLogi.dto.ManagerCreateDTO;
import com.example.SmartLogi.dto.ManagerResponseDTO;
import com.example.SmartLogi.entities.WarehouseManager;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface ManagerMapper {
    ManagerResponseDTO toDTO(WarehouseManager manager);
    WarehouseManager toEntity(ManagerCreateDTO managerCreateDTO);
}
