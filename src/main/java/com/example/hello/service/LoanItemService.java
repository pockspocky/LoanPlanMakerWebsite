package com.example.hello.service;

import com.example.hello.dto.LoanItemRequest;
import com.example.hello.entity.LoanItem;
import com.example.hello.entity.MonthlyPayment;
import com.example.hello.repository.LoanItemRepository;
import com.example.hello.repository.MonthlyPaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LoanItemService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanItemService.class);
    
    @Autowired
    private LoanItemRepository loanItemRepository;
    
    @Autowired
    private MonthlyPaymentRepository monthlyPaymentRepository;
    
    public LoanItem createLoanItem(LoanItemRequest request, String userId) {
        LoanItem loanItem = new LoanItem();
        loanItem.setUserId(userId);
        loanItem.setLoanName(request.getLoanName());
        loanItem.setLoanAmount(request.getLoanAmount());
        loanItem.setLoanDeadline(request.getLoanDeadline());
        loanItem.setYearlyInterestRate(request.getYearlyInterestRate());
        loanItem.setPaybackMethod(request.getPaybackMethod());
        loanItem.setLoanBank(request.getLoanBank());
        loanItem.setLoanType(request.getLoanType());
        loanItem.setStatus("pending");
        
        System.out.println("Saving loan item: " + loanItem);
        LoanItem saved = loanItemRepository.save(loanItem);
        System.out.println("Saved loan item: " + saved);
        
        // 创建月度还款计划
        createMonthlyPayments(saved);
        
        return saved;
    }
    
    private void createMonthlyPayments(LoanItem loanItem) {
        // 计算每月应还金额（简单等额本息）
        double monthlyInterestRate = loanItem.getYearlyInterestRate().doubleValue() / 12 / 100;
        double totalAmount = loanItem.getLoanAmount().doubleValue() * 
            (1 + monthlyInterestRate * loanItem.getLoanDeadline());
        double monthlyPayment = totalAmount / loanItem.getLoanDeadline();
        
        // 创建每月还款记录
        for (int i = 1; i <= loanItem.getLoanDeadline(); i++) {
            MonthlyPayment payment = new MonthlyPayment();
            payment.setLoanItemId(loanItem.getId());
            payment.setMonthIndex(i);
            payment.setPlannedAmount(BigDecimal.valueOf(monthlyPayment));
            payment.setPaymentAmount(BigDecimal.ZERO);  // 初始未还款
            
            // 设置还款日期（每月15号）
            LocalDate dueDate = LocalDate.now()
                .plusMonths(i)
                .withDayOfMonth(15);
            payment.setDueDate(dueDate);
            
            // 设置状态
            if (i == 1) {
                payment.setStatus("PENDING");  // 第一期待还
            } else {
                payment.setStatus("NOT_DUE");  // 其他期未到期
            }
            
            monthlyPaymentRepository.save(payment);
        }
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