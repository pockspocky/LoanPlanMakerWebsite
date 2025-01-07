package com.example.hello.dto;

import lombok.Data;

@Data
public class MonthlyPaymentDTO {
    private Integer monthIndex;
    private Double paymentAmount;
    private Double plannedAmount;
    private String status;
    private String statusDescription;
    private String dueDate;
} 