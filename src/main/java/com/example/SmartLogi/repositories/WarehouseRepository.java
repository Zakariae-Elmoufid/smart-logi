package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.entities.Warehouse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    List<Warehouse> findAll();

}
