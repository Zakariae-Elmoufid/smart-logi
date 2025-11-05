package com.example.SmartLogi.services;


import com.example.SmartLogi.dto.SupplierRequestDTO;
import com.example.SmartLogi.dto.SupplierResponseDTO;
import com.example.SmartLogi.entities.Supplier;
import com.example.SmartLogi.exception.BusinessException;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.SupplierMapper;
import com.example.SmartLogi.repositories.SupplierRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    @Autowired
    private  SupplierMapper supplierMapper;

    public SupplierResponseDTO create(SupplierRequestDTO dto) {
        if (supplierRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new BusinessException("Supplier with this phone number already exists!");
        }
        Supplier supplier = supplierMapper.toEntity(dto);
        return supplierMapper.toDto(supplierRepository.save(supplier));
    }


    public List<SupplierResponseDTO> getAll() {
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toDto)
                .toList();
    }

    public SupplierResponseDTO getById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return supplierMapper.toDto(supplier);
    }

    public SupplierResponseDTO update(Long id, SupplierRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        supplierMapper.updateEntityFromDto(dto, supplier);
        return supplierMapper.toDto(supplierRepository.save(supplier));
    }

    public SupplierResponseDTO  delete(Long id) throws BadRequestException {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

             supplierRepository.delete(supplier);
        return supplierMapper.toDto(supplier);


    }

}
