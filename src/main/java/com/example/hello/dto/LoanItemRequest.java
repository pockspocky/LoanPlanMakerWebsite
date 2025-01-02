package com.example.hello.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanItemRequest {
    private BigDecimal loanAmount;
    private Integer loanDeadline;
    private BigDecimal yearlyInterestRate;
    private String paybackMethod;
} 