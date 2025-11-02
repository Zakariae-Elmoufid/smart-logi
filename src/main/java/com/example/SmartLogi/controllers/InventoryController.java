package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.*;
import com.example.SmartLogi.services.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inventories")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody InventoryRequestDTO dto) {
        InventoryResponseDTO inventory =  inventoryService.create(dto);
        ApiResponse response = ApiResponse.builder().message("Inventory created successfully!")
                .data(inventory)
                .status(HttpStatus.CREATED.value()).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getInventoryById(@PathVariable long id ) {
        InventoryResponseDTO inventory  = inventoryService.getById(id);
        ApiResponse response = ApiResponse.builder()
                .message("Inventory retrieved successfully!")
                .data(inventory)
                .status(HttpStatus.OK.value()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getInventorys() {
        List<InventoryResponseDTO> inventorys = inventoryService.getAll();
        ApiResponse response = ApiResponse.builder()
                .message("Inventorys retrieved successfully!")
                .data(inventorys)
                .status(HttpStatus.OK.value()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/inbound")
    public ResponseEntity<ApiResponse> recordInbound(@Valid @RequestBody InventoryMovementRequestDTO dto) {
        InventoryMovementResponseDTO inventoryMovement =  inventoryService.recordInbound(dto);
        ApiResponse response = ApiResponse.builder()
                .message("Inbound recorded successfully")
                .data(inventoryMovement)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/outbound")
    public ResponseEntity<ApiResponse> recordOutbound(@Valid @RequestBody InventoryMovementRequestDTO dto) {
        InventoryMovementResponseDTO inventoryMovement = inventoryService.recordOutbound(dto);
        ApiResponse response = ApiResponse.builder()
                .message("Outbound recorded successfully")
                .data(inventoryMovement)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/adjustment")
    public  ResponseEntity<ApiResponse> recordAdjustment(@Valid @RequestBody InventoryMovementRequestDTO dto){
        InventoryMovementResponseDTO  inventoryMovment = inventoryService.recordAdjustment(dto);
        ApiResponse response = ApiResponse.builder()
                .message("Adjustment recorded successfully")
                .data(inventoryMovment)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
