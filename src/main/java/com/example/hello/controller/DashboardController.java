package com.example.hello.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/loan/add")
    public String addLoan() {
        return "loan-add";
    }

    @GetMapping("/loan/{id}/details")
    public String loanDetails(@PathVariable String id, Model model) {
        model.addAttribute("loanId", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("userId", auth.getName());
        return "loan-details";
    }
} 