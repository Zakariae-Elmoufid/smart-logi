package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.PurchaseOrderRequestDTO;
import com.example.SmartLogi.dto.PurchaseOrderResponseDTO;
import com.example.SmartLogi.repositories.PurchaseOrderRepository;
import com.example.SmartLogi.services.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequestDTO dto) {
        PurchaseOrderResponseDTO purchase = purchaseOrderService.create(dto);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("Order created successfully")
                .status(HttpStatus.CREATED.value())
                .data(purchase)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPurchaseOrders() {
        List<PurchaseOrderResponseDTO> orders = purchaseOrderService.getAll();
        ApiResponse apiResponse = ApiResponse.builder()
                .message("order found ")
                .status(HttpStatus.CREATED.value())
                .data(orders)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPurchaseOrderById( @PathVariable Long id) {
        PurchaseOrderResponseDTO oder = purchaseOrderService.getById(id);
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Purchase order retrieved successfully")
                .data(oder)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

//    @PutMapping("/{id}")
//    public  ResponseEntity<ApiResponse> updatePurchaseOrder(@PathVariable Long id,
//        @Valid @RequestBody PurchaseOrderRequestDTO dto) {
//        PurchaseOrderResponseDTO updatedOrder = purchaseOrderService.update(id,dto);
//        ApiResponse apiResponse = ApiResponse.builder()
//                .status(HttpStatus.OK.value())
//                .message("Purchase order updated successfully")
//                .data(updatedOrder)
//                .build();
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }

}
