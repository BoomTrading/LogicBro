package com.example.LogicBro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @GetMapping
    public String projects(Model model, Principal principal) {
        // Add user info to model if needed
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "projects";
    }

    @GetMapping("/{id}")
    public String projectDetails(@PathVariable String id, Model model) {
        // For now, redirect to dashboard
        // Later implement actual project details page
        model.addAttribute("projectId", id);
        return "redirect:/dashboard";
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createProject(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam String type,
            Principal principal) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // TODO: Implement actual project creation logic
            // For now, just return success
            response.put("success", true);
            response.put("message", "Project created successfully");
            response.put("projectId", "demo-" + System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create project: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteProject(@PathVariable String id, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // TODO: Implement actual project deletion logic
            response.put("success", true);
            response.put("message", "Project deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete project: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
