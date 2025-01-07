package com.example.hello.repository;

import com.example.hello.entity.MonthlyPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyPaymentRepository extends JpaRepository<MonthlyPayment, String> {
    List<MonthlyPayment> findByLoanItemIdOrderByMonthIndex(String loanItemId);
    Optional<MonthlyPayment> findByLoanItemIdAndMonthIndex(String loanItemId, Integer monthIndex);
} 