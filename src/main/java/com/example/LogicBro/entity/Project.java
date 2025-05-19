package com.example.LogicBro.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "project_type")
    private String projectType; // e.g., "ANALYSIS", "COMPOSITION", "MODULAR"

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<AudioFile> audioFiles = new ArrayList<>();

    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings; // JSON string for project-specific settings

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastAccessed = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}