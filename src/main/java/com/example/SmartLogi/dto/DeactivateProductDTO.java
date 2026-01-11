package com.example.SmartLogi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;



public class DeactivateProductDTO {
    @NotBlank
    private String sku;


    public DeactivateProductDTO(String sku) {
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
