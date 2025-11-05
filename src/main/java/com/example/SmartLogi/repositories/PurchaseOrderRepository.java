package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

}
