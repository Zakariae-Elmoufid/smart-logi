package com.example.SmartLogi.entities;


import com.example.SmartLogi.enums.OrderStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="sales_order")
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name="order_status")
    private OrderStatus orderStatus;


    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="reserved_at")
    private LocalDateTime reservedAt;
    @Column(name="shipped_at")
    private LocalDateTime shippedAt;
    @Column(name="delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private List<SalesOrderLine> orderLines = new ArrayList<>();


    public SalesOrder() {}
    public SalesOrder(long id, List<SalesOrderLine> orderLines, LocalDateTime deliveredAt, LocalDateTime shippedAt, LocalDateTime reservedAt, LocalDateTime createdAt, OrderStatus orderStatus, Client client) {
        this.id = id;
        this.orderLines = orderLines;
        this.deliveredAt = deliveredAt;
        this.shippedAt = shippedAt;
        this.reservedAt = reservedAt;
        this.createdAt = createdAt;
        this.orderStatus = orderStatus;
        this.client = client;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public List<SalesOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<SalesOrderLine> orderLines) {
        this.orderLines = orderLines;
    }
}
