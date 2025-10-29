package com.example.SmartLogi.mapper;


import com.example.SmartLogi.dto.ClientRequestDTO;
import com.example.SmartLogi.dto.ClientResponseDTO;
import com.example.SmartLogi.entities.Client;
import com.example.SmartLogi.enums.UserRole;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientResponseDTO toDto(Client client);
    Client toEntity(ClientRequestDTO DTO);
    @AfterMapping
    default void setDefaultRole(@MappingTarget Client client) {
        if (client.getRole() == null) {
            client.setRole(UserRole.CLIENT);
        }
    }
}
