package com.example.SmartLogi.entities;


import jakarta.persistence.*;

@Entity
@Table(name="inventorys")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="quantity_on_hand")
    private  int quantityOnHand;
    @Column(name="quantity_reserved")
    private int quantityReserved;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

}
