package com.example.hello.controller;

import com.example.hello.dto.LoanItemRequest;
import com.example.hello.service.LoanItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan-items")
public class LoanItemController {
    
    @Autowired
    private LoanItemService loanItemService;
    
    @PostMapping
    public ResponseEntity<?> createLoanItem(@RequestBody LoanItemRequest request, Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(loanItemService.createLoanItem(request, userId));
    }
    
    @GetMapping("/user/current")
    public ResponseEntity<?> getCurrentUserLoanItems(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(loanItemService.getLoanItemsByUserId(userId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLoanItem(@PathVariable String id, Authentication authentication) {
        String userId = authentication.getName();
        loanItemService.deleteLoanItem(id, userId);
        return ResponseEntity.ok().build();
    }
} 