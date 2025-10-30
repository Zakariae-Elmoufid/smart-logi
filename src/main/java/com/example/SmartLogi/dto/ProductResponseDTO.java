package com.example.SmartLogi.dto;

import java.time.LocalDateTime;

public record ProductResponseDTO(long id , long categoryId, String name, String sku,double purchasePrice, double sellingPrice, boolean active,
                                 LocalDateTime createdAt) {
}


