package com.example.SmartLogi.services;

import com.example.SmartLogi.entities.SalesOrder;
import com.example.SmartLogi.enums.OrderStatus;
import com.example.SmartLogi.repositories.SalesOrderRepository;
import com.example.SmartLogi.repositories.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservationScheduler.class);

    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private InventoryService inventoryService;

    // Runs every hour (3600000 ms)
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void releaseExpiredReservations() {
        try {
            LocalDateTime threshold = LocalDateTime.now().minusHours(24);

            List<SalesOrder> expired = salesOrderRepository
                    .findByOrderStatusAndReservedAtBefore(OrderStatus.RESERVED, threshold);

            expired.forEach(order -> {
                inventoryService.releaseInventory(order);
                shipmentRepository.markOrderNotConfirmed(order.getId());
                order.setOrderStatus(OrderStatus.CREATED);
                salesOrderRepository.save(order);
            });

            if (!expired.isEmpty()) {
                log.info("Released {} expired reservations.", expired.size());
            }

        } catch (Exception e) {
            log.error("Error in scheduled task: {}", e.getMessage(), e);
        }
    }
}
