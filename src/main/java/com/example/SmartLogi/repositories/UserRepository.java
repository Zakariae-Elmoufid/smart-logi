package com.example.SmartLogi.repositories;

import org.springframework.data.repository.CrudRepository;
import com.example.SmartLogi.entities.User;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
