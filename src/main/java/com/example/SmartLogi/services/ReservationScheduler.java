package com.example.SmartLogi.services;


import com.example.SmartLogi.entities.SalesOrder;
import com.example.SmartLogi.enums.OrderStatus;
import com.example.SmartLogi.repositories.SalesOrderRepository;
import com.example.SmartLogi.repositories.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationScheduler {

    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private InventoryService inventoryService;

    @Scheduled(fixedRate = 3600000)
    public void releaseExpiredReservations() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<SalesOrder> expired = salesOrderRepository
                .findByOrderStatusAndReservedAtBefore(OrderStatus.RESERVED, threshold);

        expired.forEach(order -> {
            inventoryService.releaseInventory(order);

            shipmentRepository.markOrderNotConfirmed(order.getId());

            order.setOrderStatus(OrderStatus.CREATED);
            salesOrderRepository.save(order);
        });
    }
}
