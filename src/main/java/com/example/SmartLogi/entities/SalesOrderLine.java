package com.example.SmartLogi.entities;


import com.example.SmartLogi.enums.OrderLineStatus;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="sales_order_line")
public class SalesOrderLine {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="quantity_requested")
    private Integer  quantityRequested;

    @Column(name="quantity_reserved")
    private Integer  quantityReserved;

    @Column(name="quantity_backorder")
    private Integer  quantityBackorder;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private OrderLineStatus status;

    private double price;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;




}
