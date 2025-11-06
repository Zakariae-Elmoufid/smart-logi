package com.example.SmartLogi.entities;

import com.example.SmartLogi.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "clients")
public class Client extends User {

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private List<SalesOrder> orders;

    // Constructors
//    public Client() {}
//
//    public Client(String firstName, String lastName, String email, String password, String phoneNumber) {
//        super(firstName, lastName, email, password, UserRole.CLIENT);
//        this.phoneNumber = phoneNumber;
//    }

    // Getter & Setter


//    public String getPhoneNumber() { return phoneNumber; }
//    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
