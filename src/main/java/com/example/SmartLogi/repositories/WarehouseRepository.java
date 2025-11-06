package com.example.SmartLogi.repositories;

import com.example.SmartLogi.dto.WarehouseInventoryProjection;
import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.entities.Warehouse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    List<Warehouse> findAll();
    List<Warehouse> findAllById(Iterable<Long> ids);

    @Query("""
        SELECT     w.id AS warehouseId,\s
                    i.id AS inventoryId,\s
                    (i.quantityOnHand - i.quantityReserved) AS quantityHand
        FROM Warehouse w
        JOIN w.inventories i
        JOIN i.product p
        WHERE p.id = :id
        """)
    List<WarehouseInventoryProjection> findWarehousesByProductId(@Param("id") long id);


}
