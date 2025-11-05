package com.example.SmartLogi.services;


import com.example.SmartLogi.dto.WarehouseRequestDTO;
import com.example.SmartLogi.dto.WarehouseResponseDTO;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.mapper.WarehouseMapper;
import com.example.SmartLogi.repositories.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;


    @Autowired
    private WarehouseMapper warehouseMapper;


    public WarehouseResponseDTO create(WarehouseRequestDTO dto) {
        Warehouse warehouse = warehouseMapper.toEntity(dto);
        return warehouseMapper.toDTO(warehouseRepository.save(warehouse));
    }


    public List<WarehouseResponseDTO> getAll() {
        return  warehouseRepository.findAll().stream()
                .map(warehouseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public WarehouseResponseDTO getWarehouseById(long id){
        return warehouseMapper.toDTO(warehouseRepository.findById(id).orElse(null));
    }

    public WarehouseResponseDTO updateWarehouse(long id , WarehouseRequestDTO dto) {
        Warehouse warehouse =   warehouseRepository.findById(id).orElse(null);
        warehouse.setName(dto.name());
        warehouse.setAddress(dto.address());
        warehouse.setCode(dto.code());
        warehouse.setActive(dto.active());
        return warehouseMapper.toDTO(warehouseRepository.save(warehouse));
    }

    public boolean deleteWarehouse(long id) {
        if(!warehouseRepository.existsById(id)) return false;
        warehouseRepository.deleteById(id);
        return  true;
    }

}
