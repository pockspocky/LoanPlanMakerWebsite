package com.example.hello.controller;

import com.example.hello.dto.PaymentSummaryResponse;
import com.example.hello.dto.ErrorResponse;
import com.example.hello.dto.PaymentRequest;
import com.example.hello.dto.StatusUpdateRequest;
import com.example.hello.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan-items")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/{loanItemId}/payment-summary")
    public ResponseEntity<?> getPaymentSummary(
            @PathVariable String loanItemId,
            @RequestParam String userId) {
        try {
            PaymentSummaryResponse summary = paymentService.getPaymentSummary(loanItemId, userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{loanItemId}/monthly-payment")
    public ResponseEntity<?> addPayment(
            @PathVariable String loanItemId,
            @RequestBody PaymentRequest request) {
        try {
            PaymentSummaryResponse summary = paymentService.addPayment(
                loanItemId, 
                request.getMonthIndex(), 
                request.getAmount()
            );
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{loanItemId}/monthly-payment/{monthIndex}/status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable String loanItemId,
            @PathVariable Integer monthIndex,
            @RequestBody StatusUpdateRequest request) {
        try {
            PaymentSummaryResponse summary = paymentService.updatePaymentStatus(
                loanItemId,
                monthIndex,
                request.getStatus()
            );
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
} 