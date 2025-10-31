package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ManagerRequestDTO;
import com.example.SmartLogi.dto.ManagerResponseDTO;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.entities.WarehouseManager;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.mapper.ManagerMapper;
import com.example.SmartLogi.repositories.WarehouseManagerRepository;
import com.example.SmartLogi.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class WarehouseManagerService {

    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;
    
    @Autowired
    private ManagerMapper managerMapper;

    public ManagerResponseDTO create(ManagerRequestDTO dto ) {
         WarehouseManager warehouseManager =  WarehouseManager.builder()
                 .firstName(dto.firstName())
                 .lastName(dto.firstName())
                 .email(dto.email())
                 .password(PasswordUtils.hash(dto.password()))
                 .role(UserRole.WAREHOUSE_MANAGER)
                 .createdAt(LocalDateTime.now())
                 .active(true)
                 .build();
        return managerMapper.toDTO(warehouseManagerRepository.save(warehouseManager));

    }
}
