package com.example.SmartLogi.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name ="supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="supplier_name")
    private String supplierName;
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "supplier")
    private List<PurchaseOrder> purchaseOrders;

    public Supplier() {}
}
