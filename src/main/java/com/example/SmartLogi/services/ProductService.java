package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.ProductRequestDTO;
import com.example.SmartLogi.dto.ProductResponseDTO;
import com.example.SmartLogi.entities.Category;
import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.mapper.ProductMapper;
import com.example.SmartLogi.repositories.CategoryRepository;
import com.example.SmartLogi.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProductService {

     @Autowired
    private ProductRepository repository;

     @Autowired
     private CategoryRepository categoryRepository;

     @Autowired
    private   ProductMapper mapper;

    public ProductResponseDTO createProduct(ProductRequestDTO dto){
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = Product.builder()
                .sku(dto.sku())
                .name(dto.name())
                .category(category) // set the real entity
                .purchasePrice(dto.purchasePrice())
                .sellingPrice(dto.sellingPrice())
                .active(dto.active())
                .createdAt(LocalDateTime.now())
                .build();

        return mapper.toDTO(repository.save(product));
    }

    public List<ProductResponseDTO> getAllProducts(){
        return  repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ProductResponseDTO getProductById(Long id) {
        Optional<Product> product = repository.findById(id);
        return product.map(mapper::toDTO).orElse(null);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product product = repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));


        product.setName(dto.name());
        product.setSku(dto.sku());
        product.setPurchasePrice(dto.purchasePrice());
        product.setSellingPrice(dto.sellingPrice());
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);
        product.setActive(dto.active());

        return mapper.toDTO(repository.save(product));
    }


    public boolean deleteProduct(Long id) {
            if (!repository.existsById(id)) return false;
            repository.deleteById(id);
            return true;

    }





}
