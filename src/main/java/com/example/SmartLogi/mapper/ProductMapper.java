package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.ProductRequestDTO;
import com.example.SmartLogi.dto.ProductResponseDTO;
import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.repositories.ProductRepository;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product  toEntity(ProductRequestDTO dto);
    ProductResponseDTO toDTO(Product product);

}
