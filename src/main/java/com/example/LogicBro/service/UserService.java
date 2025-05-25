package com.example.LogicBro.service;

import com.example.LogicBro.entity.User;
import com.example.LogicBro.exception.UserNotFoundException;
import com.example.LogicBro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Service class for managing user-related operations and Spring Security authentication.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with the provided username, password, and email.
     *
     * @param username the username of the new user
     * @param password the password to be encoded
     * @param email    the email address of the user
     * @throws IllegalArgumentException if username, password, or email is null or empty
     */
    @Transactional
    public void registerUser(String username, String password, String email) {
        validateUserInput(username, password, email);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
    }

    /**
     * Loads a user by username for Spring Security authentication.
     *
     * @param username the username identifying the user
     * @return the UserDetails object for the user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        validateUsername(username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
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
     * Validates user input for registration.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @param email    the email to validate
     * @throws IllegalArgumentException if any input is null or empty
     */
    private void validateUserInput(String username, String password, String email) {
        validateUsername(username);
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }
}