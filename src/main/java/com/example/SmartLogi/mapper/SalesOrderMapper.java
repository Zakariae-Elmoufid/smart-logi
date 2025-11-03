package com.example.SmartLogi.mappers;


import com.example.SmartLogi.entities.SalesOrderLine;
import com.example.SmartLogi.mapper.SalesOrderLineMapper;
import org.mapstruct.Mapper;
import com.example.SmartLogi.entities.SalesOrder;
import com.example.SmartLogi.dto.SalesOrderResponseDTO;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = SalesOrderLineMapper.class)

public interface SalesOrderMapper {
    @Mapping(target = "clientId", source = "client.id")
    SalesOrderResponseDTO toDTO(SalesOrder order);

}
