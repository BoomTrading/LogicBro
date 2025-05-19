package com.example.LogicBro.controller;

import com.example.LogicBro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String email, Model model) {
        try {
            userService.registerUser(username, password, email);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Username or email may already exist.");
            return "register";
        }
    }
}