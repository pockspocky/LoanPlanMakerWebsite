package com.example.hello.controller;

import com.example.hello.dto.LoanItemRequest;
import com.example.hello.entity.LoanItem;
import com.example.hello.service.LoanItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan-items")
public class LoanItemController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanItemController.class);
    
    @Autowired
    private LoanItemService loanItemService;
    
    @PostMapping
    public ResponseEntity<?> createLoanItem(@RequestBody LoanItemRequest request, Authentication authentication) {
        try {
            logger.info("Creating loan item for user: {}", authentication.getName());
            logger.info("Request data: {}", request);
            
            String userId = authentication.getName();
            LoanItem loanItem = loanItemService.createLoanItem(request, userId);
            
            logger.info("Loan item created successfully: {}", loanItem);
            return ResponseEntity.ok(loanItem);
        } catch (Exception e) {
            logger.error("Error creating loan item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating loan item: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/current")
    public ResponseEntity<?> getCurrentUserLoanItems(Authentication authentication) {
        try {
            String userId = authentication.getName();
            logger.info("Fetching loan items for user: {}", userId);
            
            List<LoanItem> items = loanItemService.getLoanItemsByUserId(userId);
            logger.info("Found {} loan items", items.size());
            
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error fetching loan items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching loan items: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserLoanItems(@PathVariable String username) {
        try {
            logger.info("Admin fetching loan items for user: {}", username);
            
            List<LoanItem> items = loanItemService.getLoanItemsByUserId(username);
            logger.info("Found {} loan items for user {}", items.size(), username);
            
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error fetching loan items for user: " + username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching loan items: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLoanItem(@PathVariable String id, Authentication authentication) {
        try {
            String userId = authentication.getName();
            System.out.println("接收到删除请求 - ID: " + id + ", 用户: " + userId);
            
            if (!loanItemService.existsById(id)) {
                System.out.println("贷款项目不存在 - ID: " + id);
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("开始执行删除操作");
            loanItemService.deleteLoanItem(id, userId);
            System.out.println("删除操作完成");
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("删除操作失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("删除贷款项目失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")  // 只允许管理员访问
    public ResponseEntity<?> getAllLoanItems() {
        try {
            logger.info("Admin fetching all loan items");
            List<LoanItem> items = loanItemService.getAllLoanItems();
            logger.info("Found {} loan items", items.size());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error fetching all loan items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching loan items: " + e.getMessage());
        }
    }
} 