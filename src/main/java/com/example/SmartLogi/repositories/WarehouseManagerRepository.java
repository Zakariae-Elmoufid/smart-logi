package com.example.SmartLogi.repositories;

import com.example.SmartLogi.controllers.WarehouseManagerController;
import com.example.SmartLogi.entities.WarehouseManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseManagerRepository extends JpaRepository<WarehouseManager,Long> {
}
