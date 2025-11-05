package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.PurchaseOrderLineRsponseDTO;
import com.example.SmartLogi.entities.PurchaseOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface PurchaseOrderLineMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    PurchaseOrderLineRsponseDTO toDTO(PurchaseOrderLine line);
    List<PurchaseOrderLineRsponseDTO> toDTO(List<PurchaseOrderLine> lines);
}
