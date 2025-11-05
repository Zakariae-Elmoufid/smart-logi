package com.example.SmartLogi.mapper;


import org.mapstruct.Mapper;
import com.example.SmartLogi.entities.SalesOrder;
import com.example.SmartLogi.dto.SalesOrderResponseDTO;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SalesOrderLineMapper.class)
public interface SalesOrderMapper {
    @Mapping(target = "clientId", source = "client.id")
    SalesOrderResponseDTO toDTO(SalesOrder order);

}
