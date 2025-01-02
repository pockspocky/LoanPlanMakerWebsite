package com.example.hello.service;

import com.example.hello.dto.LoanItemRequest;
import com.example.hello.entity.LoanItem;
import com.example.hello.repository.LoanItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanItemService {
    
    @Autowired
    private LoanItemRepository loanItemRepository;
    
    public LoanItem createLoanItem(LoanItemRequest request, String userId) {
        LoanItem loanItem = new LoanItem();
        loanItem.setUserId(userId);
        loanItem.setLoanAmount(request.getLoanAmount());
        loanItem.setLoanDeadline(request.getLoanDeadline());
        loanItem.setYearlyInterestRate(request.getYearlyInterestRate());
        loanItem.setPaybackMethod(request.getPaybackMethod());
        loanItem.setStatus("pending");
        
        return loanItemRepository.save(loanItem);
    }
    
    public List<LoanItem> getLoanItemsByUserId(String userId) {
        return loanItemRepository.findByUserId(userId);
    }
    
    public void deleteLoanItem(String id, String userId) {
        LoanItem loanItem = loanItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("贷款事项不存在"));
            
        if (!loanItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此贷款事项");
        }
        
        loanItemRepository.delete(loanItem);
    }
} 