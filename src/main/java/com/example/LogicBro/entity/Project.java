package com.example.LogicBro.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "projects")
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "created_at")
    private Timestamp createdAt;
}