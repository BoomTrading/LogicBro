package com.example.LogicBro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.LogicBro.service.AudioStorageService;

@Controller
public class FileUploadController {

    private final AudioStorageService audioStorageService;

    public FileUploadController(AudioStorageService audioStorageService) {
        this.audioStorageService = audioStorageService;
    }

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        try {
            String filename = audioStorageService.storeAudio(file);
            redirectAttributes.addFlashAttribute("message", "File " + filename + " uploaded successfully!");
            return "redirect:/upload";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
            return "redirect:/upload";
        }
    }
}