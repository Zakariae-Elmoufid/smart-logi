package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.*;
import com.example.SmartLogi.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/inventories")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody InventoryRequestDTO dto) {
        InventoryResponseDTO inventory =  inventoryService.create(dto);
        ApiResponse response = ApiResponse.builder().message("Inventory created successfully!")
                .data(inventory)
                .status(HttpStatus.CREATED.value()).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/inbound")
    public ResponseEntity<ApiResponse> recordInbound(@RequestBody InventoryMovementRequestDTO dto) {
        InventoryMovementResponseDTO inventoryMovement =  inventoryService.recordInbound(dto);
        ApiResponse response = ApiResponse.builder()
                .message("Inbound recorded successfully")
                .data(inventoryMovement)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
