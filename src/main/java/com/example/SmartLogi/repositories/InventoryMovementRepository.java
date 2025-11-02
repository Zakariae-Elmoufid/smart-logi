package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository  extends JpaRepository<InventoryMovement, Long> {
}
