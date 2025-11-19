package com.example.SmartLogi.controllers;

import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.SupplierRequestDTO;
import com.example.SmartLogi.dto.SupplierResponseDTO;
import com.example.SmartLogi.services.SupplierService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.EntityResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid SupplierRequestDTO dto) {
        SupplierResponseDTO supplier = supplierService.create(dto);
        ApiResponse response = ApiResponse.builder()
                .message("Successfully created supplier")
                .status(HttpStatus.CREATED.value())
                .data(supplier)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        List<SupplierResponseDTO >suppliers =  supplierService.getAll();
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .data(suppliers)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        SupplierResponseDTO supplier =  supplierService.getById(id);
            ApiResponse response = ApiResponse.builder()
                    .message("Successfully retrieved supplier")
                    .status(HttpStatus.OK.value())
                    .data(supplier)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ApiResponse>update(@PathVariable Long id, @RequestBody @Valid SupplierRequestDTO dto) {
        SupplierResponseDTO supplier =  supplierService.update(id, dto);
        ApiResponse response = ApiResponse.builder()
                .message("Successfully updated supplier")
                .status(HttpStatus.OK.value())
                .data(supplier)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) throws BadRequestException {
        SupplierResponseDTO deletedSupplier = supplierService.delete(id);
        ApiResponse response = ApiResponse.builder()
                .message("Supplier deleted successfully!")
                .data(deletedSupplier)
                .status(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
