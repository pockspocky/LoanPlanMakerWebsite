package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_items")
@Data
@EntityListeners(AuditingEntityListener.class)
public class LoanItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String loanName;
    
    @Column(nullable = false)
    private BigDecimal loanAmount;
    
    @Column(nullable = false)
    private Integer loanDeadline;
    
    @Column(nullable = false)
    private BigDecimal yearlyInterestRate;
    
    @Column(nullable = false)
    private String paybackMethod;
    
    @Column(nullable = false)
    private String loanBank;
    
    @Column(nullable = false)
    private String loanType;
    
    @Column(nullable = false)
    private String status;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
} 