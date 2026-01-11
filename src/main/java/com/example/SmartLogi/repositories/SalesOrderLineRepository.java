package com.example.SmartLogi.repositories;


import com.example.SmartLogi.entities.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {

    List<SalesOrderLine> findAllByProductSku(String sku);
}
