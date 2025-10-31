package com.example.SmartLogi.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private String message;
    private Object data;
    private int status;
}
