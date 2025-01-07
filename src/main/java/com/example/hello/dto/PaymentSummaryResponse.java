package com.example.hello.dto;

import lombok.Data;
import java.util.List;

@Data
public class PaymentSummaryResponse {
    private Double loanAmount;
    private Double totalAmount;
    private Double totalInterest;
    private Double totalPaid;
    private Double remainingAmount;
    private Double completionRate;
    private List<MonthlyPaymentDTO> monthlyPayments;
} 