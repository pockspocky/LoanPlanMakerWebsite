package com.example.hello.controller;

import com.example.hello.entity.User;
import com.example.hello.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserManagementController {

    @Autowired
    private UserService userService;

    @GetMapping("/user-management")
    public String userManagement() {
        return "user-management";
    }

    @GetMapping("/user/{username}/loans")
    public String userLoans(@PathVariable String username, Model model) {
        model.addAttribute("username", username);
        return "user-loans";
    }

    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/api/users/{username}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        if ("admin".equals(username)) {
            return ResponseEntity.badRequest().body("Cannot delete admin user");
        }
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
} 