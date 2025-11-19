package com.example.SmartLogi.mapper;


import com.example.SmartLogi.dto.CategoryRequestDTO;
import com.example.SmartLogi.dto.CategoryResponseDTO;
import com.example.SmartLogi.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
     CategoryResponseDTO toDto(Category category);
    Category  toEntity(CategoryRequestDTO dto);

}
