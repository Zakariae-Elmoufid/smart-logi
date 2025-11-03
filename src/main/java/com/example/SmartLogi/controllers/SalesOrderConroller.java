package com.example.SmartLogi.controllers;

import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.SalesOrderRequestDTO;
import com.example.SmartLogi.dto.SalesOrderResponseDTO;
import com.example.SmartLogi.entities.SalesOrder;
import com.example.SmartLogi.repositories.SalesOrderRepository;
import com.example.SmartLogi.services.SalesOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/client/salse-order")
public class SalesOrderConroller {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody SalesOrderRequestDTO dto) {
        SalesOrderResponseDTO salesOrder = salesOrderService.create(dto);
        ApiResponse response = ApiResponse.builder()
                .message(salesOrder.message())
                .data(salesOrder)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse> getSalesOrder(@Valid @PathVariable Long id){
        SalesOrderResponseDTO salesOrder = salesOrderService.confirmOrder(id);
        ApiResponse response = ApiResponse.builder()
                .message(salesOrder.message())
                .data(salesOrder)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



}
