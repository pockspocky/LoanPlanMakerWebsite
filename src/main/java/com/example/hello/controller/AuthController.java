package com.example.hello.controller;

import com.example.hello.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private InMemoryUserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterRequest registerRequest, Model model) {
        // 验证密码是否匹配
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            model.addAttribute("error", "两次输入的密码不匹配");
            return "register";
        }

        // 检查用户名是否已存在
        try {
            userDetailsManager.loadUserByUsername(registerRequest.getUsername());
            model.addAttribute("error", "用户名已存在");
            return "register";
        } catch (Exception e) {
            // 用户名不存在，可以创建新用户
            UserDetails user = User.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .roles("USER")
                .build();
            
            userDetailsManager.createUser(user);
            return "redirect:/login?registered";
        }
    }
} 