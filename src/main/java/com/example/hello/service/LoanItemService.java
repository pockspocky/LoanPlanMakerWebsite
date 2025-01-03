package com.example.hello.service;

import com.example.hello.dto.LoanItemRequest;
import com.example.hello.entity.LoanItem;
import com.example.hello.repository.LoanItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LoanItemService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanItemService.class);
    
    @Autowired
    private LoanItemRepository loanItemRepository;
    
    public LoanItem createLoanItem(LoanItemRequest request, String userId) {
        LoanItem loanItem = new LoanItem();
        loanItem.setUserId(userId);
        loanItem.setLoanName(request.getLoanName());
        loanItem.setLoanAmount(request.getLoanAmount());
        loanItem.setLoanDeadline(request.getLoanDeadline());
        loanItem.setYearlyInterestRate(request.getYearlyInterestRate());
        loanItem.setPaybackMethod(request.getPaybackMethod());
        loanItem.setStatus("pending");
        
        System.out.println("Saving loan item: " + loanItem);
        LoanItem saved = loanItemRepository.save(loanItem);
        System.out.println("Saved loan item: " + saved);
        
        return saved;
    }
    
    public List<LoanItem> getLoanItemsByUserId(String userId) {
        return loanItemRepository.findByUserId(userId);
    }
    
    public boolean existsById(String id) {
        return loanItemRepository.existsById(id);
    }
    
    @Transactional
    public void deleteLoanItem(String id, String userId) {
        System.out.println("开始删除贷款项目 - ID: " + id + ", 用户: " + userId);
        
        System.out.println("查找贷款项目");
        LoanItem loanItem = loanItemRepository.findById(id)
            .orElseThrow(() -> {
                System.out.println("贷款项目不存在 - ID: " + id);
                return new RuntimeException("贷款事项不存在");
            });
            
        System.out.println("找到贷款项目 - ID: " + id + ", 所有者: " + loanItem.getUserId());
        
        // 检查用户权限（管理员可以删除任何贷款）
        if (!userId.equals("admin") && !loanItem.getUserId().equals(userId)) {
            System.out.println("用户无权删除此贷款项目 - ID: " + id + ", 请求用户: " + userId + ", 所有者: " + loanItem.getUserId());
            throw new RuntimeException("无权删除此贷款事项");
        }
        
        System.out.println("开始删除贷款项目");
        try {
            loanItemRepository.delete(loanItem);
            System.out.println("数据库删除操作执行完成");
        } catch (Exception e) {
            System.out.println("数据库删除操作失败: " + e.getMessage());
            throw e;
        }
        System.out.println("贷款项目删除成功");
    }
    
    public List<LoanItem> getAllLoanItems() {
        return loanItemRepository.findAll();
    }
} 