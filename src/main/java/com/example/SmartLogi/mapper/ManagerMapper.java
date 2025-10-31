package com.example.SmartLogi.mapper;


import com.example.SmartLogi.dto.ManagerRequestDTO;
import com.example.SmartLogi.dto.ManagerResponseDTO;
import com.example.SmartLogi.entities.WarehouseManager;
import org.apache.catalina.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")

public interface ManagerMapper {
    ManagerResponseDTO toDTO(WarehouseManager manager);
    WarehouseManager toEntity(ManagerRequestDTO managerRequestDTO);
}
