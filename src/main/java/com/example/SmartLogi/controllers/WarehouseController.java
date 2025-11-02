package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.ProductResponseDTO;
import com.example.SmartLogi.dto.WarehouseRequestDTO;
import com.example.SmartLogi.dto.WarehouseResponseDTO;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.repositories.WarehouseRepository;
import com.example.SmartLogi.services.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<ApiResponse> createWarehouse(@Valid @RequestBody WarehouseRequestDTO dto) {
            WarehouseResponseDTO warehouse = warehouseService.create(dto);
        ApiResponse response =  ApiResponse.builder().message("Warehouse  created successfully!")
                .data(warehouse)
                .status(HttpStatus.CREATED.value()).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllWarehouses() {
        List<WarehouseResponseDTO> warehouses = warehouseService.getAll();
        ApiResponse response =  ApiResponse.builder().message("All Warehouses ")
                .data(warehouses)
                .status(HttpStatus.CREATED.value()).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getWarehouses(@PathVariable  Long id) {
        WarehouseResponseDTO warehouse = warehouseService.getWarehouseById(id);
        if (warehouse == null) {
            ApiResponse response = ApiResponse.builder()
                    .message("Warehouse not found")
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        ApiResponse response = ApiResponse.builder()
                .message("Warehouse found")
                .data(warehouse)
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseRequestDTO dto) {
        WarehouseResponseDTO warehouse = warehouseService.updateWarehouse(id, dto);
        if (warehouse == null) {
            ApiResponse response = ApiResponse.builder()
                    .message("Warehouse not found")
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        ApiResponse response = ApiResponse.builder()
                .message("Warehouse found")
                .data(warehouse)
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<ApiResponse> deleteWarehouse(@PathVariable Long id) {
        boolean deleted = warehouseService.deleteWarehouse(id);
        ApiResponse response = ApiResponse.builder()
                .message(deleted ? "Warehouse deleted successfully" : "Warehouse not found")
                .status(deleted ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(response, deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);

    }


}
