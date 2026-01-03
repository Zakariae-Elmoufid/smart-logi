package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ProductRequestDTO;
import com.example.SmartLogi.dto.ProductResponseDTO;
import com.example.SmartLogi.entities.Category;
import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.mapper.ProductMapper;
import com.example.SmartLogi.repositories.CategoryRepository;
import com.example.SmartLogi.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;
    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = Product.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Laptop")
                .category(category)
                .purchasePrice(800.0)
                .sellingPrice(1200.0)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        // ProductRequestDTO: name, sku, purchasePrice, sellingPrice, active, categoryId
        requestDTO = new ProductRequestDTO(
                "Laptop", "SKU-001", 800.0, 1200.0, true, 1L
        );

        // ProductResponseDTO: id, categoryName, name, sku, purchasePrice, sellingPrice, active, createdAt
        responseDTO = new ProductResponseDTO(
                1L, "Electronics", "Laptop", "SKU-001",
                800.0, 1200.0, true, LocalDateTime.now()
        );
    }

    @Test
    void createProduct_ShouldReturnProductResponseDTO() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.save(any(Product.class))).thenReturn(product);
        when(mapper.toDTO(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.createProduct(requestDTO);

        assertNotNull(result);
        assertEquals("Laptop", result.name());
        assertEquals("SKU-001", result.sku());
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        ProductRequestDTO invalidRequest = new ProductRequestDTO(
                "Laptop", "SKU-001", 800.0, 1200.0, true, 99L
        );

        assertThrows(RuntimeException.class, 
            () -> productService.createProduct(invalidRequest));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        when(repository.findAll()).thenReturn(List.of(product));
        when(mapper.toDTO(product)).thenReturn(responseDTO);

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).name());
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(mapper.toDTO(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getProductById_ShouldReturnNull_WhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ProductResponseDTO result = productService.getProductById(99L);

        assertNull(result);
    }

    @Test
    void updateProduct_ShouldUpdateProduct_WhenFound() {
        ProductRequestDTO updateDTO = new ProductRequestDTO(
                "Desktop", "SKU-002", 600.0, 900.0, true, 1L
        );
        ProductResponseDTO updatedResponse = new ProductResponseDTO(
                1L, "Electronics", "Desktop", "SKU-002",
                600.0, 900.0, true, LocalDateTime.now()
        );

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.save(any(Product.class))).thenReturn(product);
        when(mapper.toDTO(any(Product.class))).thenReturn(updatedResponse);

        ProductResponseDTO result = productService.updateProduct(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Desktop", result.name());
        verify(repository, times(1)).save(product);
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> productService.updateProduct(99L, requestDTO));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenCategoryNotFound() {
        ProductRequestDTO invalidRequest = new ProductRequestDTO(
                "Laptop", "SKU-001", 800.0, 1200.0, true, 99L
        );

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> productService.updateProduct(1L, invalidRequest));
    }
}
