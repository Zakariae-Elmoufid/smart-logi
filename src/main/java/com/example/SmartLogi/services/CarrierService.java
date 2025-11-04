package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.CarrierRequestDTO;
import com.example.SmartLogi.dto.CarrierResponseDTO;
import com.example.SmartLogi.entities.Carrier;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.CarrierMapper;
import com.example.SmartLogi.repositories.CarrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrierService {

    @Autowired
    private CarrierRepository carrierRepository;

    @Autowired
    private CarrierMapper carrierMapper;

    public CarrierResponseDTO create(CarrierRequestDTO dto) {
        Carrier carrier = carrierMapper.toEntity(dto);
        Carrier saved = carrierRepository.save(carrier);
        return carrierMapper.toDTO(saved);
    }

    public List<CarrierResponseDTO> getAll() {
        return carrierRepository.findAll().stream().map(carrierMapper::toDTO).toList();
    }

    public CarrierResponseDTO getById(Long id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        return carrierMapper.toDTO(carrier);
    }

    public CarrierResponseDTO update(Long id, CarrierRequestDTO dto) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));

        carrier.setCarrierName(dto.carrierName());
        carrier.setPhoneNumber(dto.phoneNumber());

        Carrier updated = carrierRepository.save(carrier);
        return carrierMapper.toDTO(updated);
    }

    public void delete(Long id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        carrierRepository.delete(carrier);
    }
}
