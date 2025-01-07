package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "monthly_payments")
@Data
@NoArgsConstructor
public class MonthlyPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String loanItemId;
    
    @Column(nullable = false)
    private Integer monthIndex;
    
    @Column(nullable = false)
    private BigDecimal plannedAmount;
    
    @Column(nullable = false)
    private BigDecimal paymentAmount;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    @Column(nullable = false)
    private String status; // OVERDUE, PENDING, NOT_DUE
} 