package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository  extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndWarehouseId(long productId, long warehouseId);
}
