package com.example.SmartLogi.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="inventorys")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="quantity_on_hand")
    private  int quantityOnHand;
    @Column(name="quantity_reserved")
    private Integer quantityReserved;


    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;


    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    @JsonIgnore
    private Warehouse warehouse;

}
