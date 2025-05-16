package com.example.LogicBro.repository;

import com.example.LogicBro.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}