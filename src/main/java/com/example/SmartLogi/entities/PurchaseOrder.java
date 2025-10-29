package com.example.SmartLogi.entities;


import com.example.SmartLogi.enums.OrderStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="purchase_order")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Column(name="order_date")
    private LocalDateTime orderDate;


    @ManyToOne
    @JoinColumn(name="supplier_id")
    private Supplier supplier;


    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderLine> orderLines = new ArrayList<>();

    public PurchaseOrder() {}
    public PurchaseOrder(Long id, List<PurchaseOrderLine> orderLines, LocalDateTime orderDate, Supplier supplier, OrderStatus orderStatus) {
        this.id = id;
        this.orderLines = orderLines;
        this.orderDate = orderDate;
        this.supplier = supplier;
        this.orderStatus = orderStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<PurchaseOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<PurchaseOrderLine> orderLines) {
        this.orderLines = orderLines;
    }
}
