package com.example.hello.service;

import com.example.hello.dto.PaymentSummaryResponse;
import com.example.hello.dto.MonthlyPaymentDTO;
import com.example.hello.entity.LoanItem;
import com.example.hello.entity.MonthlyPayment;
import com.example.hello.repository.LoanItemRepository;
import com.example.hello.repository.MonthlyPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    // 定义状态常量
    private static final String STATUS_OVERDUE = "OVERDUE";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PAID = "PAID";

    // 定义还款方式常量
    private static final String METHOD_EQUAL_INSTALLMENT = "等额本息";
    private static final String METHOD_EQUAL_PRINCIPAL = "等额本金";
    private static final String METHOD_INTEREST_FIRST = "先息后本";
    private static final String METHOD_ONE_TIME = "一次性还本付息";

    @Autowired
    private LoanItemRepository loanItemRepository;
    
    @Autowired
    private MonthlyPaymentRepository monthlyPaymentRepository;

    public PaymentSummaryResponse getPaymentSummary(String loanItemId, String userId) {
        // 验证贷款事项是否存在
        LoanItem loanItem = loanItemRepository.findById(loanItemId)
            .orElseThrow(() -> new RuntimeException("贷款事项不存在"));

        // 验证用户权限
        if (!userId.equals("admin") && !loanItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权查询此贷款事项");
        }

        PaymentSummaryResponse response = new PaymentSummaryResponse();
        
        // 获取月度还款记录
        List<MonthlyPayment> payments = monthlyPaymentRepository.findByLoanItemIdOrderByMonthIndex(loanItemId);
        
        // 如果没有月度还款记录，则创建
        if (payments.isEmpty()) {
            payments = createInitialMonthlyPayments(loanItem);
        }
        
        // 设置贷款本金
        response.setLoanAmount(loanItem.getLoanAmount().doubleValue());
        
        // 计算总利息
        double totalInterest = calculateTotalInterest(loanItem);
        response.setTotalInterest(totalInterest);
        
        // 设置本息合计
        response.setTotalAmount(response.getLoanAmount() + response.getTotalInterest());
        
        // 计算已还总额
        double totalPaid = payments.stream()
            .mapToDouble(p -> p.getPaymentAmount().doubleValue())
            .sum();
        response.setTotalPaid(totalPaid);
        
        // 计算剩余应还金额
        response.setRemainingAmount(response.getTotalAmount() - response.getTotalPaid());
        
        // 计算完成率
        double completionRate = (response.getTotalPaid() / response.getTotalAmount()) * 100;
        response.setCompletionRate(Math.round(completionRate * 10.0) / 10.0);
        
        // 设置月度还款计划
        response.setMonthlyPayments(payments.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
        
        return response;
    }
    
    private double calculateTotalInterest(LoanItem loanItem) {
        return loanItem.getLoanAmount().doubleValue() * 
            (loanItem.getYearlyInterestRate().doubleValue() / 100) * 
            (loanItem.getLoanDeadline() / 12.0);
    }
    
    private List<MonthlyPayment> createInitialMonthlyPayments(LoanItem loanItem) {
        List<MonthlyPayment> payments = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        // 获取贷款基本信息
        double principal = loanItem.getLoanAmount().doubleValue();
        double yearlyRate = loanItem.getYearlyInterestRate().doubleValue() / 100;
        int months = loanItem.getLoanDeadline();
        String paybackMethod = loanItem.getPaybackMethod();
        
        // 根据还款方式处理每月还款金额
        if ("一次性还本付息".equals(paybackMethod)) {
            // 只在最后一个月还款
            double totalAmount = principal * (1 + yearlyRate * months / 12);
            for (int i = 1; i <= months; i++) {
                MonthlyPayment payment = new MonthlyPayment();
                payment.setLoanItemId(loanItem.getId());
                payment.setMonthIndex(i);
                if (i == months) {
                    payment.setPlannedAmount(BigDecimal.valueOf(totalAmount));
                } else {
                    payment.setPlannedAmount(BigDecimal.ZERO);
                }
                payment.setPaymentAmount(BigDecimal.ZERO);
                payment.setDueDate(now.plusMonths(i - 1).withDayOfMonth(15));
                payment.setStatus(getInitialStatus(payment.getDueDate(), now));
                payments.add(monthlyPaymentRepository.save(payment));
            }
        } else if ("先息后本".equals(paybackMethod)) {
            // 每月还利息，最后一个月还本金
            double monthlyInterest = principal * yearlyRate / 12;
            for (int i = 1; i <= months; i++) {
                MonthlyPayment payment = new MonthlyPayment();
                payment.setLoanItemId(loanItem.getId());
                payment.setMonthIndex(i);
                if (i == months) {
                    payment.setPlannedAmount(BigDecimal.valueOf(monthlyInterest + principal));
                } else {
                    payment.setPlannedAmount(BigDecimal.valueOf(monthlyInterest));
                }
                payment.setPaymentAmount(BigDecimal.ZERO);
                payment.setDueDate(now.plusMonths(i - 1).withDayOfMonth(15));
                payment.setStatus(getInitialStatus(payment.getDueDate(), now));
                payments.add(monthlyPaymentRepository.save(payment));
            }
        } else if ("等额本金".equals(paybackMethod)) {
            // 每月本金固定，利息逐月递减
            double monthlyPrincipal = principal / months;
            double remainingPrincipal = principal;
            for (int i = 1; i <= months; i++) {
                MonthlyPayment payment = new MonthlyPayment();
                payment.setLoanItemId(loanItem.getId());
                payment.setMonthIndex(i);
                double monthlyInterest = remainingPrincipal * yearlyRate / 12;
                payment.setPlannedAmount(BigDecimal.valueOf(monthlyPrincipal + monthlyInterest));
                payment.setPaymentAmount(BigDecimal.ZERO);
                payment.setDueDate(now.plusMonths(i - 1).withDayOfMonth(15));
                payment.setStatus(getInitialStatus(payment.getDueDate(), now));
                payments.add(monthlyPaymentRepository.save(payment));
                remainingPrincipal -= monthlyPrincipal;
            }
        } else {
            // 等额本息
            double monthlyPayment = calculateMonthlyPayment(loanItem);
            for (int i = 1; i <= months; i++) {
                MonthlyPayment payment = new MonthlyPayment();
                payment.setLoanItemId(loanItem.getId());
                payment.setMonthIndex(i);
                payment.setPlannedAmount(BigDecimal.valueOf(monthlyPayment));
                payment.setPaymentAmount(BigDecimal.ZERO);
                payment.setDueDate(now.plusMonths(i - 1).withDayOfMonth(15));
                payment.setStatus(getInitialStatus(payment.getDueDate(), now));
                payments.add(monthlyPaymentRepository.save(payment));
            }
        }
        
        return payments;
    }
    
    private double calculateMonthlyPayment(LoanItem loanItem) {
        String paybackMethod = loanItem.getPaybackMethod();
        double principal = loanItem.getLoanAmount().doubleValue();
        double yearlyRate = loanItem.getYearlyInterestRate().doubleValue() / 100;
        int months = loanItem.getLoanDeadline();
        
        switch (paybackMethod) {
            case "等额本息":
                // 等额本息公式：每月还款额 = [贷款本金×月利率×(1+月利率)^还款月数]÷[(1+月利率)^还款月数-1]
                double monthlyRate = yearlyRate / 12;
                if (monthlyRate == 0) {
                    return principal / months;
                } else {
                    double temp = Math.pow(1 + monthlyRate, months);
                    return principal * monthlyRate * temp / (temp - 1);
                }
                
            case "等额本金":
                // 每月本金固定，利息逐月递减
                double monthlyPrincipal = principal / months;
                // 对于第一个月的还款额
                return monthlyPrincipal + principal * yearlyRate / 12;
                
            case "先息后本":
                // 每月只还利息，最后一个月还本金
                double monthlyInterest = principal * yearlyRate / 12;
                return monthlyInterest;
                
            case "一次性还本付息":
                // 到期一次性还本付息
                double totalAmount = principal * (1 + yearlyRate * months / 12);
                return totalAmount;
                
            default:
                // 默认按等额本息计算
                monthlyRate = yearlyRate / 12;
                if (monthlyRate == 0) {
                    return principal / months;
                }
                double defaultTemp = Math.pow(1 + monthlyRate, months);
                return principal * monthlyRate * defaultTemp / (defaultTemp - 1);
        }
    }
    
    private MonthlyPaymentDTO convertToDTO(MonthlyPayment payment) {
        MonthlyPaymentDTO dto = new MonthlyPaymentDTO();
        dto.setMonthIndex(payment.getMonthIndex());
        dto.setPaymentAmount(payment.getPaymentAmount().doubleValue());
        dto.setPlannedAmount(payment.getPlannedAmount().doubleValue());
        dto.setStatus(payment.getStatus());
        dto.setStatusDescription(getStatusDescription(payment.getStatus()));
        dto.setDueDate(payment.getDueDate().toString());
        return dto;
    }
    
    private String getStatusDescription(String status) {
        switch (status) {
            case STATUS_OVERDUE: return "逾期";
            case STATUS_PENDING: return "待还";
            case STATUS_PAID: return "已还完";
            default: return "待还";
        }
    }
    
    private String getInitialStatus(LocalDate dueDate, LocalDate now) {
        if (dueDate.isBefore(now)) {
            return STATUS_OVERDUE;
        } else {
            return STATUS_PENDING;
        }
    }

    @Transactional
    public PaymentSummaryResponse addPayment(String loanItemId, int monthIndex, double amount) {
        MonthlyPayment payment = monthlyPaymentRepository.findByLoanItemIdAndMonthIndex(loanItemId, monthIndex)
            .orElseThrow(() -> new RuntimeException("找不到对应的还款记录"));
            
        // 更新还款金额
        BigDecimal newAmount = payment.getPaymentAmount().add(BigDecimal.valueOf(amount));
        payment.setPaymentAmount(newAmount);
        
        // 更新状态
        if (newAmount.compareTo(payment.getPlannedAmount()) == 0) {
            payment.setStatus(STATUS_PAID);
        } else if (newAmount.compareTo(payment.getPlannedAmount()) < 0) {
            payment.setStatus(STATUS_PENDING);
        } else if (newAmount.compareTo(payment.getPlannedAmount()) > 0) {
            throw new RuntimeException("还款金额不能超过计划还款金额");
        }
        
        monthlyPaymentRepository.save(payment);
        
        // 返回更新后的汇总信息
        return getPaymentSummary(loanItemId, "admin");
    }

    @Transactional
    public PaymentSummaryResponse updatePaymentStatus(String loanItemId, int monthIndex, String newStatus) {
        MonthlyPayment payment = monthlyPaymentRepository.findByLoanItemIdAndMonthIndex(loanItemId, monthIndex)
            .orElseThrow(() -> new RuntimeException("找不到对应的还款记录"));
            
        if (payment.getStatus().equals(STATUS_PAID)) {
            throw new RuntimeException("已还完的记录不能修改状态");
        }
        
        payment.setStatus(newStatus);
        monthlyPaymentRepository.save(payment);
        
        return getPaymentSummary(loanItemId, "admin");
    }
} 