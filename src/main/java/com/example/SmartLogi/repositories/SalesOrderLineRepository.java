package com.example.SmartLogi.repositories;


import com.example.SmartLogi.entities.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {
}
