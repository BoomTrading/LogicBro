package com.example.LogicBro.controller;

import com.example.LogicBro.entity.Project;
import com.example.LogicBro.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("projects", projectRepository.findAll());
        return "dashboard";
    }
}