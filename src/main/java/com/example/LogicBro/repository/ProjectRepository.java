package com.example.LogicBro.repository;

import com.example.LogicBro.entity.Project;
import com.example.LogicBro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUser(User user);
}