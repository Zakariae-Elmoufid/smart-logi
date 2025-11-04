package com.example.SmartLogi.dto;

import com.example.SmartLogi.entities.Client;
import com.example.SmartLogi.entities.SalesOrder;
import com.example.SmartLogi.entities.SalesOrderLine;
import com.example.SmartLogi.enums.OrderStatus;

import java.util.List;

public record  SalesOrderResponseDTO
        (long id,
         long clientId,
         OrderStatus orderStatus,
         List<SalesOrderLineResponseDTO>orderLines,
         String message
         )
{
}
