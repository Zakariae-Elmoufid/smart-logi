package com.example.SmartLogi.mapper;


import com.example.SmartLogi.dto.CategoryRequestDTO;
import com.example.SmartLogi.dto.CategoryResponseDTO;
import com.example.SmartLogi.entities.Category;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
     CategoryResponseDTO toDto(Category category);
    Category  toEntity(CategoryRequestDTO dto);

}
