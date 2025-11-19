package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.ProductRequestDTO;
import com.example.SmartLogi.dto.ProductResponseDTO;
import com.example.SmartLogi.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product  toEntity(ProductRequestDTO dto);
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponseDTO toDTO(Product product);

}
