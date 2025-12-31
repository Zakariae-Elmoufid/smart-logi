package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ManagerCreateDTO;
import com.example.SmartLogi.dto.ManagerResponseDTO;
import com.example.SmartLogi.dto.ManagerUpdateDTO;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.entities.WarehouseManager;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.mapper.ManagerMapper;
import com.example.SmartLogi.repositories.WarehouseManagerRepository;
import com.example.SmartLogi.repositories.WarehouseRepository;
import com.example.SmartLogi.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class WarehouseManagerService {

    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private ManagerMapper managerMapper;

    public ManagerResponseDTO create(ManagerCreateDTO dto ) {

        Set<Warehouse> warehouses = new HashSet<>(warehouseRepository.findAllById(dto.warehouseIds()));

        WarehouseManager warehouseManager =  WarehouseManager.builder()
                 .firstName(dto.firstName())
                 .lastName(dto.firstName())
                 .email(dto.email())
//                 .password(PasswordUtils.hash(dto.password()))
                 .role(UserRole.WAREHOUSE_MANAGER)
                 .createdAt(LocalDateTime.now())
                 .enabled(true)
                 .build();
                    warehouses.stream().forEach(warehouse -> {
                     warehouseManager.addWarehouse(warehouse);
                    });
        return managerMapper.toDTO(warehouseManagerRepository.save(warehouseManager));
    }

    public ManagerResponseDTO getManagerById(long id) {
        return managerMapper.toDTO(warehouseManagerRepository.findById(id).orElse(null));
    }

     public ManagerResponseDTO updateManager(long id , ManagerUpdateDTO dto) {
         WarehouseManager warehouseManager = warehouseManagerRepository.findById(id).orElse(null);
         warehouseManager.setFirstName(dto.firstName());
         warehouseManager.setLastName(dto.lastName());
         warehouseManager.setEmail(dto.email());
         warehouseManager.setEnabled(dto.active());
         return managerMapper.toDTO(warehouseManagerRepository.save(warehouseManager));
     }

     public boolean deleteManager(long id) {
         if(!warehouseManagerRepository.existsById(id)) return false;
         warehouseManagerRepository.deleteById(id);
         return  true;
     }

}
