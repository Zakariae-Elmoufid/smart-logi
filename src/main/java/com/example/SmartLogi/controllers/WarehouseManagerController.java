package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.ManagerRequestDTO;
import com.example.SmartLogi.dto.ManagerResponseDTO;
import com.example.SmartLogi.services.WarehouseManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/managers")
public class WarehouseManagerController {

    @Autowired
    private WarehouseManagerService managerService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ManagerRequestDTO dto) {
        ManagerResponseDTO warehouseManager =   managerService.create(dto);
        ApiResponse response =  ApiResponse.builder().message("Warehouse manager created successfully!")
                .data(warehouseManager)
                .status(HttpStatus.CREATED.value()).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
