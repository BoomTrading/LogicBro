package com.example.LogicBro.service;

import com.example.LogicBro.entity.Project;
import com.example.LogicBro.entity.User;
import com.example.LogicBro.exception.UserNotFoundException;
import com.example.LogicBro.repository.ProjectRepository;
import com.example.LogicBro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Service class for managing Project entities.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves all projects associated with the specified username.
     * Ensures the requesting user matches the authenticated user.
     *
     * @param username the username of the user whose projects are to be retrieved
     * @return a list of projects owned by the user
     * @throws UserNotFoundException if the user is not found
     * @throws AccessDeniedException if the username does not match the authenticated user
     */
    @Transactional(readOnly = true)
    public List<Project> getUserProjects(String username) {
        validateUsername(username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        validateAuthenticatedUser(username);
        return projectRepository.findByUser(user);
    }

    /**
     * Deletes a project by its ID, ensuring the requesting user owns the project.
     *
     * @param projectId the ID of the project to delete
     * @param username  the username of the user requesting deletion
     * @throws IllegalArgumentException if the project is not found
     * @throws AccessDeniedException    if the user does not own the project
     */
    @Transactional
    public void deleteProject(Long projectId, String username) {
        validateUsername(username);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        if (!project.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You don't have permission to delete this project");
        }
        projectRepository.delete(project);
    }

    /**
     * Creates a new project for the specified user.
     *
     * @param name the name of the project
     * @param user the user who owns the project
     * @return the created project
     */
    @Transactional
    public Project createProject(String name, User user) {
        Objects.requireNonNull(name, "Project name cannot be null");
        Objects.requireNonNull(user, "User cannot be null");
        Project project = new Project();
        project.setProjectName(name);
        project.setUser(user);
        return projectRepository.save(project);
    }

    /**
     * Validates that the username is not null or empty.
     *
     * @param username the username to validate
     * @throws IllegalArgumentException if the username is null or empty
     */
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }

    /**
     * Validates that the provided username matches the authenticated user.
     *
     * @param username the username to validate
     * @throws AccessDeniedException if the username does not match the authenticated user
     */
    private void validateAuthenticatedUser(String username) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(authenticatedUsername)) {
            throw new AccessDeniedException("Unauthorized access: username does not match authenticated user");
        }
    }
}