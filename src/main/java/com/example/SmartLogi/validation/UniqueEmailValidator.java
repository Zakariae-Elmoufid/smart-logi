package com.example.SmartLogi.validation;

import com.example.SmartLogi.repositories.ClientRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueEmailValidator  implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) return true; // Not responsible for @NotBlank
        return !clientRepository.existsByEmail(email);
    }
}
