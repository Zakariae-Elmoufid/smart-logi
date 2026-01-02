package com.example.SmartLogi.repositories;

import com.example.SmartLogi.entities.SalesOrder;
import com.example.SmartLogi.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesOrderRepository  extends JpaRepository<SalesOrder,Long> {
    public List<SalesOrder> findByOrderStatusAndReservedAtBefore(OrderStatus status, LocalDateTime threshold);

    public List<SalesOrder> findByWarehouse_IdAndOrderStatus(long warehouseId,OrderStatus orderStatus);
}
