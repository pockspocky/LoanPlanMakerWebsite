package com.example.hello.repository;

import com.example.hello.entity.LoanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanItemRepository extends JpaRepository<LoanItem, String> {
    List<LoanItem> findByUserId(String userId);
} 