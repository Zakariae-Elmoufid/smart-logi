package com.example.SmartLogi.services;


import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.AssignCarrierRequestDTO;
import com.example.SmartLogi.entities.Carrier;
import com.example.SmartLogi.entities.Shipment;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.repositories.CarrierRepository;
import com.example.SmartLogi.repositories.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ShipmentService {

    @Autowired
   private ShipmentRepository shipmentRepository;
    @Autowired
   private CarrierRepository carrierRepository;

   public void   assignCarrier(long shipmentId ,long carrierId){
       Shipment shipment = shipmentRepository.findById(shipmentId)
               .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id " + shipmentId));

       Carrier carrier = carrierRepository.findById(carrierId)
               .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id " + carrierId));

       shipment.setCarrier(carrier);
       shipmentRepository.save(shipment);
   }
}
