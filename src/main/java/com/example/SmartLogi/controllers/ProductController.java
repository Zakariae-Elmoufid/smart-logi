package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.ProductRequestDTO;
import com.example.SmartLogi.dto.ProductResponseDTO;
import com.example.SmartLogi.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;


    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO product = productService.createProduct(dto);

        ApiResponse response =  ApiResponse.builder().message("Product created successfully!")
                .data(product)
                .status(HttpStatus.CREATED.value()).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        ApiResponse response =  ApiResponse.builder().message("All products")
                .data(products)
                .status(HttpStatus.CREATED.value()).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);

        if (product == null) {
            ApiResponse response = ApiResponse.builder()
                    .message("Product not found")
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ApiResponse response = ApiResponse.builder()
                .message("Product found")
                .data(product)
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO product = productService.updateProduct(id, dto);


        if (product == null) {
            ApiResponse response = ApiResponse.builder()
                    .message("Product not found")
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ApiResponse response = ApiResponse.builder()
                .message("Product found")
                .data(product)
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }





}
