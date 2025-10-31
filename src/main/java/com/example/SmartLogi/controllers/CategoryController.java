package com.example.SmartLogi.controllers;

import com.example.SmartLogi.dto.CategoryRequestDTO;
import com.example.SmartLogi.dto.CategoryResponseDTO;
import com.example.SmartLogi.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    @Autowired
    private  CategoryService categoryService;


    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
         CategoryResponseDTO createdCategory = categoryService.createCategory(dto);
         return ResponseEntity.ok(createdCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody CategoryRequestDTO dto) {
        categoryService.update(id,dto);
        return ResponseEntity.ok("Category updated successfully!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok("Category Deleted successfully!");
    }



}
