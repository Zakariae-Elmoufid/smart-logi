package com.example.SmartLogi.entities;


import com.example.SmartLogi.enums.ShipmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="shipment")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="shipment_status")
    private ShipmentStatus shipmentStatus;

    @Column(name="planned_date")
    private LocalDateTime plannedDate;
    @Column(name="shipped_date")
    private LocalDateTime shippedDate;
    @Column(name="delivery_date")
    private LocalDateTime deliveryDate;

    @ManyToOne
    @JoinColumn(name="carrier_id")
    private Carrier carrier;

    public Shipment() {}
    public Shipment(int id, String trackingNumber, ShipmentStatus shipmentStatus, LocalDateTime plannedDate, LocalDateTime shippedDate, LocalDateTime deliveryDate, Carrier carrier) {
        this.id = id;
        this.trackingNumber = trackingNumber;
        this.shipmentStatus = shipmentStatus;
        this.plannedDate = plannedDate;
        this.shippedDate = shippedDate;
        this.deliveryDate = deliveryDate;
        this.carrier = carrier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public LocalDateTime getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDateTime plannedDate) {
        this.plannedDate = plannedDate;
    }

    public LocalDateTime getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(LocalDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }
}

