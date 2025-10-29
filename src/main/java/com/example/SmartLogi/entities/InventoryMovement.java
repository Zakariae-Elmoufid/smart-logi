package com.example.SmartLogi.entities;


import com.example.SmartLogi.enums.MovementType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="inventory_movement")
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    private int quantity;

    private String description;

    @Column(name="created_at")
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    public InventoryMovement() {}
}
