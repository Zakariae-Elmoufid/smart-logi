package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ManagerRequestDTO;
import com.example.SmartLogi.dto.ManagerResponseDTO;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.entities.WarehouseManager;
import com.example.SmartLogi.repositories.WarehouseManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseManagerService {

    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    public ManagerResponseDTO create(ManagerRequestDTO managerRequestDTO ) {

    }
}
