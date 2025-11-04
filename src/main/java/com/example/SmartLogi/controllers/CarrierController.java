package com.example.SmartLogi.controllers;

import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.CarrierRequestDTO;
import com.example.SmartLogi.dto.CarrierResponseDTO;
import com.example.SmartLogi.services.CarrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/carriers")
public class CarrierController {

    @Autowired
    private CarrierService carrierService;

    @PostMapping
    public ResponseEntity<ApiResponse> createCarrier(@Valid @RequestBody CarrierRequestDTO dto) {
        CarrierResponseDTO carrier = carrierService.create(dto);
        ApiResponse response = ApiResponse.builder()
                .message("Carrier created successfully!")
                .data(carrier)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCarriers() {
        List<CarrierResponseDTO> carriers = carrierService.getAll();
        ApiResponse response = ApiResponse.builder()
                .message("All carriers retrieved successfully!")
                .data(carriers)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCarrierById(@PathVariable Long id) {
        CarrierResponseDTO carrier = carrierService.getById(id);
        ApiResponse response = ApiResponse.builder()
                .message("Carrier retrieved successfully!")
                .data(carrier)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCarrier(@PathVariable Long id,
                                                     @Valid @RequestBody CarrierRequestDTO dto) {
        CarrierResponseDTO updated = carrierService.update(id, dto);
        ApiResponse response = ApiResponse.builder()
                .message("Carrier updated successfully!")
                .data(updated)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCarrier(@PathVariable Long id) {
        carrierService.delete(id);
        ApiResponse response = ApiResponse.builder()
                .message("Carrier deleted successfully!")
                .status(HttpStatus.NO_CONTENT.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}
