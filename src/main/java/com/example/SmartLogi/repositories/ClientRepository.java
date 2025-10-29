package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository  extends JpaRepository<Client, Long> {

    boolean existsByEmail(String email);
}
