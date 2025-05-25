package com.example.LogicBro.controller;

import com.example.LogicBro.dto.AudioAnalysisDTO;
import com.example.LogicBro.dto.VariationRequestDTO;
import com.example.LogicBro.service.AudioAnalysisService;
import com.example.LogicBro.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AudioAnalysisService audioAnalysisService;
    private final ProjectService projectService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("projects", projectService.getUserProjects(principal.getName()));
        return "dashboard";
    }

    @PostMapping("/api/audio/upload")
    @ResponseBody
    public ResponseEntity<?> uploadAudio(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            String fileId = audioAnalysisService.storeAudioFile(file, principal.getName());
            return ResponseEntity.ok().body(new UploadResponse(fileId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/api/audio/analyze/{fileId}")
    @ResponseBody
    public CompletableFuture<AudioAnalysisDTO> analyzeAudio(@PathVariable String fileId) {
        return audioAnalysisService.analyzeAudioFile(fileId);
    }

    @PostMapping("/api/audio/generate-variation")
    @ResponseBody
    public CompletableFuture<AudioAnalysisDTO> generateVariation(@RequestBody VariationRequestDTO request) {
        return audioAnalysisService.generateVariation(
            request.getChordProgression(),
            request.getScale(),
            request.getAmount()
        );
    }

    @DeleteMapping("/api/projects/{projectId}")
    @ResponseBody
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId, Principal principal) {
        try {
            projectService.deleteProject(projectId, principal.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Helper classes for responses
    private static class UploadResponse {
        private final String fileId;

        public UploadResponse(String fileId) {
            this.fileId = fileId;
        }

        public String getFileId() {
            return fileId;
        }
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}