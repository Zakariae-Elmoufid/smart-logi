package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.Shipment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ShipmentRepository  extends CrudRepository<Shipment, Long> {

    @Modifying
    @Query("UPDATE Shipment s SET s.shipmentStatus = 'CANCELLED' WHERE s.salesOrder.id = :orderId")
    void markOrderNotConfirmed(@Param("orderId") Long orderId);
}
