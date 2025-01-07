package com.example.hello.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanItemRequest {
    private String loanName;
    private BigDecimal loanAmount;
    private Integer loanDeadline;
    private BigDecimal yearlyInterestRate;
    private String paybackMethod;
    private String loanBank;
    private String loanType;

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }
} 