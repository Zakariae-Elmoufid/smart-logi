package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.entities.Warehouse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    List<Warehouse> findAll();
    List<Warehouse> findAllById(Iterable<Long> ids);


}
