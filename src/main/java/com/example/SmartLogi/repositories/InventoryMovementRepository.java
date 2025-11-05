package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.InventoryMovement;
import com.example.SmartLogi.enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryMovementRepository  extends JpaRepository<InventoryMovement, Long> {

    @Query("SELECT COALESCE(SUM(im.quantity), 0) " +
            "FROM InventoryMovement im " +
            "JOIN im.inventory i " +
            "JOIN PurchaseOrderLine pol ON pol.product = i.product " +
            "JOIN pol.purchaseOrder po " +
            "WHERE i.product.id = :productId " +
            "AND i.warehouse.id = :warehouseId " +
            "AND po.id = :purchaseOrderId " +
            "AND im.movementType = :movementType")
    Integer getTotalReceivedQuantity(
            @Param("productId") Long productId,
            @Param("warehouseId") Long warehouseId,
            @Param("purchaseOrderId") Long purchaseOrderId,
            @Param("movementType") MovementType movementType
    );

}
