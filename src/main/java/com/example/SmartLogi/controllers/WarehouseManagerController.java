package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.ManagerCreateDTO;
import com.example.SmartLogi.dto.ManagerResponseDTO;
import com.example.SmartLogi.dto.ManagerUpdateDTO;
import com.example.SmartLogi.services.WarehouseManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/managers")
public class WarehouseManagerController {

    @Autowired
    private WarehouseManagerService managerService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ManagerCreateDTO dto) {
        ManagerResponseDTO warehouseManager =   managerService.create(dto);
        ApiResponse response =  ApiResponse.builder().message("Warehouse manager created successfully!")
                .data(warehouseManager)
                .status(HttpStatus.CREATED.value()).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getManagerById(@PathVariable  Long id) {
        ManagerResponseDTO manager = managerService.getManagerById(id);
        if (manager == null) {
            ApiResponse response = ApiResponse.builder()
                    .message("Manager  not found")
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        ApiResponse response = ApiResponse.builder()
                .message("Manager found")
                .data(manager)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable long id,@Valid @RequestBody ManagerUpdateDTO dto) {
            ManagerResponseDTO manager = managerService.updateManager(id, dto);
        if (manager == null) {
            ApiResponse response = ApiResponse.builder()
                    .message("Manager not found")
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        ApiResponse response = ApiResponse.builder()
                .message("Manager found")
                .data(manager)
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public  ResponseEntity<ApiResponse> deleteManager(@PathVariable Long id) {
        boolean deleted = managerService.deleteManager(id);
        ApiResponse response = ApiResponse.builder()
                .message(deleted ? "Manager deleted successfully" : "Manager not found")
                .status(deleted ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(response, deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

}
