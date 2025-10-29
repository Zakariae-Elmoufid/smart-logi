package com.example.SmartLogi.entities;

import com.example.SmartLogi.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "clients")
public class Client extends User {

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    // Constructors
    public Client() {}

    public Client(String firstName, String lastName, String email, String password, String phoneNumber) {
        super(firstName, lastName, email, password, UserRole.CLIENT);
        this.phoneNumber = phoneNumber;
    }

    // Getter & Setter


    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
