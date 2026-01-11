package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.CategoryRequestDTO;
import com.example.SmartLogi.dto.CategoryResponseDTO;
import com.example.SmartLogi.entities.Category;
import com.example.SmartLogi.mapper.CategoryMapper;
import com.example.SmartLogi.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequestDTO requestDTO;
    private CategoryResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic products");
        category.setActive(true);

        requestDTO = new CategoryRequestDTO("Electronics", "Electronic products", true);
        responseDTO = new CategoryResponseDTO(1L, "Electronics", "Electronic products", true);
    }

    @Test
    void createCategory_ShouldReturnCategoryResponseDTO() {
        when(categoryMapper.toEntity(requestDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.createCategory(requestDTO);

        assertNotNull(result);
        assertEquals("Electronics", result.name());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void getAll_ShouldReturnListOfCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(responseDTO);

        List<CategoryResponseDTO> result = categoryService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).name());
    }

    @Test
    void getById_ShouldReturnCategory_WhenFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.getById(99L));
    }

    @Test
    void update_ShouldUpdateCategory_WhenFound() {
        CategoryRequestDTO updateDTO = new CategoryRequestDTO("Furniture", "Home furniture", true);
        CategoryResponseDTO updatedResponse = new CategoryResponseDTO(1L, "Furniture", "Home furniture", true);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(updatedResponse);

        CategoryResponseDTO result = categoryService.update(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Furniture", result.name());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void update_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> categoryService.update(99L, requestDTO));
    }

    @Test
    void delete_ShouldDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
