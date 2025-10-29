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
    public Inventory() {}
    public Inventory(Long id, Warehouse warehouse, Product product, int quantityReserved, int quantityOnHand) {
        this.id = id;
        this.warehouse = warehouse;
        this.product = product;
        this.quantityReserved = quantityReserved;
        this.quantityOnHand = quantityOnHand;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public int getQuantityReserved() {
        return quantityReserved;
    }

    public void setQuantityReserved(int quantityReserved) {
        this.quantityReserved = quantityReserved;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
