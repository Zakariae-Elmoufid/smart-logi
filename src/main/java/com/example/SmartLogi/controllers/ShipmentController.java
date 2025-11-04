package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.AssignCarrierRequestDTO;
import com.example.SmartLogi.entities.Shipment;
import com.example.SmartLogi.services.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;



    @PutMapping("/assign-carrier")
    public ResponseEntity<ApiResponse> assignCarrier(@RequestBody AssignCarrierRequestDTO dto) {
        shipmentService.assignCarrier(dto.shipmentId(), dto.carrierId());
        ApiResponse response = ApiResponse.builder()
                .message("Shipment assigned successfully")
                .status(200)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
