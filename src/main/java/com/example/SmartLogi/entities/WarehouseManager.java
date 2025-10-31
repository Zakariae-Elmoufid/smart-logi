package com.example.SmartLogi.entities;

import com.example.SmartLogi.entities.Inventory;
import com.example.SmartLogi.entities.Warehouse;
import com.example.SmartLogi.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import com.example.SmartLogi.entities.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "warehouse_manager")
public class WarehouseManager
        extends User
{


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "manager_warehouse",
            joinColumns = @JoinColumn(name = "manager_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "warehouse_id", referencedColumnName = "id")
    )
    private Set<Warehouse> warehouses = new HashSet<>();

    public void addWarehouse(Warehouse warehouse) {
        this.warehouses.add(warehouse);
        warehouse.getManagers().add(this);
    }

    public void removeWarehouse(Warehouse warehouse) {
        this.warehouses.remove(warehouse);
        warehouse.getManagers().remove(this);
    }

    // include id from superclass for equals/hashCode via Lombok explicit include
    @EqualsAndHashCode.Include
    private Long getIdentity() {
        return this.getId();
    }


}
