package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.CategoryRequestDTO;
import com.example.SmartLogi.dto.CategoryResponseDTO;
import com.example.SmartLogi.entities.Category;
import com.example.SmartLogi.mapper.CategoryMapper;
import com.example.SmartLogi.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {


    @Autowired
    private  CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper ;

    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        Category category = categoryMapper.toEntity(dto);
        return   categoryMapper.toDto(categoryRepository.save(category));
    }


    public List<CategoryResponseDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public CategoryResponseDTO getById(Long id) {
         Category category = categoryRepository.findById(id)
                 .orElseThrow(() -> new RuntimeException("Product not found"));;
         return categoryMapper.toDto(category);
    }

    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("category not found"));

        category.setName(dto.name());
        category.setDescription(dto.description());
        category.setActive(dto.active());

        return categoryMapper.toDto(categoryRepository.save(category));
    }


    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

}
