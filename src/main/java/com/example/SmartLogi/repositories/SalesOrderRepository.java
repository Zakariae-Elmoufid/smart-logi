package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRepository  extends JpaRepository<SalesOrder,Long> {
}
