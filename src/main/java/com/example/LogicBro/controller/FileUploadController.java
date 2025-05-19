package com.example.LogicBro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileUploadController {

    private final String uploadDir = "uploads";

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Create uploads directory if it doesn't exist
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file
            Path path = Paths.get(uploadDir + File.separator + file.getOriginalFilename());
            Files.write(path, file.getBytes());

            redirectAttributes.addFlashAttribute("message", "File uploaded successfully!");
            return "redirect:/upload";
            
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
            return "redirect:/upload";
        }
    }
}