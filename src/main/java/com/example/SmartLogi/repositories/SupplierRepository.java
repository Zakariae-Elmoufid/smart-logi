package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {
    boolean existsByPhoneNumber(String phoneNumber);

}
