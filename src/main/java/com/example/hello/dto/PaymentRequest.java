package com.example.hello.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Integer monthIndex;
    private Double amount;
} 