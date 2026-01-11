package com.example.SmartLogi.services;

import com.example.SmartLogi.entities.SalesOrder;
import com.example.SmartLogi.enums.OrderStatus;
import com.example.SmartLogi.repositories.SalesOrderRepository;
import com.example.SmartLogi.repositories.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationSchedulerTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private ReservationScheduler reservationScheduler;

    private SalesOrder expiredOrder;

    @BeforeEach
    void setUp() {
        expiredOrder = SalesOrder.builder()
                .id(1L)
                .orderStatus(OrderStatus.RESERVED)
                .reservedAt(LocalDateTime.now().minusHours(25))
                .build();
    }

    @Test
    void releaseExpiredReservations_ShouldReleaseExpiredOrders() {
        when(salesOrderRepository.findByOrderStatusAndReservedAtBefore(
                eq(OrderStatus.RESERVED), any(LocalDateTime.class)))
                .thenReturn(List.of(expiredOrder));
        doNothing().when(inventoryService).releaseInventory(expiredOrder);
        doNothing().when(shipmentRepository).markOrderNotConfirmed(1L);
        when(salesOrderRepository.save(expiredOrder)).thenReturn(expiredOrder);

        reservationScheduler.releaseExpiredReservations();

        verify(inventoryService, times(1)).releaseInventory(expiredOrder);
        verify(shipmentRepository, times(1)).markOrderNotConfirmed(1L);
        verify(salesOrderRepository, times(1)).save(expiredOrder);
    }

    @Test
    void releaseExpiredReservations_ShouldDoNothing_WhenNoExpiredOrders() {
        when(salesOrderRepository.findByOrderStatusAndReservedAtBefore(
                eq(OrderStatus.RESERVED), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        reservationScheduler.releaseExpiredReservations();

        verify(inventoryService, never()).releaseInventory(any());
        verify(shipmentRepository, never()).markOrderNotConfirmed(anyLong());
    }

    @Test
    void releaseExpiredReservations_ShouldHandleException() {
        when(salesOrderRepository.findByOrderStatusAndReservedAtBefore(
                eq(OrderStatus.RESERVED), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Should not throw exception
        reservationScheduler.releaseExpiredReservations();

        verify(inventoryService, never()).releaseInventory(any());
    }

    @Test
    void releaseExpiredReservations_ShouldProcessMultipleOrders() {
        SalesOrder secondExpiredOrder = SalesOrder.builder()
                .id(2L)
                .orderStatus(OrderStatus.RESERVED)
                .reservedAt(LocalDateTime.now().minusHours(30))
                .build();

        when(salesOrderRepository.findByOrderStatusAndReservedAtBefore(
                eq(OrderStatus.RESERVED), any(LocalDateTime.class)))
                .thenReturn(List.of(expiredOrder, secondExpiredOrder));
        doNothing().when(inventoryService).releaseInventory(any(SalesOrder.class));
        doNothing().when(shipmentRepository).markOrderNotConfirmed(anyLong());
        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(i -> i.getArgument(0));

        reservationScheduler.releaseExpiredReservations();

        verify(inventoryService, times(2)).releaseInventory(any(SalesOrder.class));
        verify(shipmentRepository, times(2)).markOrderNotConfirmed(anyLong());
        verify(salesOrderRepository, times(2)).save(any(SalesOrder.class));
    }
}
