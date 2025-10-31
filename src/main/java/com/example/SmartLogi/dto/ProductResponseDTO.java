package com.example.SmartLogi.dto;

import com.example.SmartLogi.entities.Category;

import java.time.LocalDateTime;
import java.util.Locale;

public record ProductResponseDTO(long id , String categoryName, String name, String sku, double purchasePrice, double sellingPrice, boolean active,
                                 LocalDateTime createdAt) {
}


