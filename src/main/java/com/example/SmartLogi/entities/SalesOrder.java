package com.example.SmartLogi.entities;


import com.example.SmartLogi.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name ="sales_order")
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(name="order_status")
    private OrderStatus orderStatus;



    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="confirmed_at")
    private LocalDateTime confirmedAt;
    @Column(name="reserved_at")
    private LocalDateTime reservedAt;
    @Column(name="shipped_at")
    private LocalDateTime shippedAt;
    @Column(name="delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "salesOrder", fetch = FetchType.EAGER)
    private List<SalesOrderLine> orderLines = new ArrayList<>();



}
