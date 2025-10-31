package com.example.SmartLogi.mapper;


import com.example.SmartLogi.dto.ManagerRequestDTO;
import com.example.SmartLogi.dto.ManagerResponseDTO;
import org.apache.catalina.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")

public interface ManagerMapper {
    ManagerResponseDTO toDTO(Manager manager);
    Manager toEntity(ManagerRequestDTO managerRequestDTO);
}
