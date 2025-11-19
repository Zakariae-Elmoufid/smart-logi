package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.SalesOrderLineResponseDTO;
import com.example.SmartLogi.entities.SalesOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalesOrderLineMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    SalesOrderLineResponseDTO toDTO(SalesOrderLine line);
    List<SalesOrderLineResponseDTO> toDTO(List<SalesOrderLine> lines);
}
